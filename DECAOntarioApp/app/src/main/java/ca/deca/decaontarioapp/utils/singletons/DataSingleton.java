package ca.deca.decaontarioapp.utils.singletons;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.commons.codec.binary.Base64;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.deca.decaontarioapp.objects.Event;
import ca.deca.decaontarioapp.objects.Tweet;
import ca.deca.decaontarioapp.utils.NetworkUtils;
import de.greenrobot.event.EventBus;

/**
 * Handles all calls for data, including caching and networking
 * <br>This is a Singleton, so the first call initializes the class, and
 * all others access the same instance
 */
public class DataSingleton {

    // The object that makes this a singleton
    private static DataSingleton mInstance;

    // The base file directory
    String base;

    private Context mContext;
    private AsyncTask async = null;

    // Represents the state of the device's network connection
    private boolean NETWORK_FLAG = false;

    // The file names for the data files
    private final String FILE_FLAG_START = "flagstart.txt",
            FILE_ABOUT = "about.json", FILE_EXECS = "execs.json",
            FILE_REGIONALS = "regionals.json", FILE_EVENTS = "events.json",
            FILE_TWITTER = "twitter.json", FILE_PROVINCIALS = "provincials.json";

    // The urls for the data files
    private final String URL_ABOUT = "https://raw.githubusercontent.com/renonat/deca-ontario-android-app/master/assets/about.json",
            URL_EVENTS = "https://raw.githubusercontent.com/renonat/deca-ontario-android-app/master/assets/events.json",
            URL_EXECS = "https://raw.githubusercontent.com/renonat/deca-ontario-android-app/master/assets/execs.json",
            URL_PROVINCIALS = "https://raw.githubusercontent.com/renonat/deca-ontario-android-app/master/assets/provincials.json",
            URL_REGIONALS = "https://raw.githubusercontent.com/renonat/deca-ontario-android-app/master/assets/regionals.json";

    /**
     * A private constructor for the class
     */
    private DataSingleton() {
    }

    /**
     * Gets the current instance of the class, and creates it if it does not exist
     *
     * @return {@link DataSingleton} : the instance
     * of DataSingleton
     */
    public static DataSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new DataSingleton();
        }
        return mInstance;
    }

    /**
     * Refreshes the cache with new files from the network
     *
     * @param ctxt {@link Context} : The context of the parent activity
     */
    public void Initialize(Context ctxt) {
        // Only run the cache task once per application cycle
        // Async is only automatically started if Singleton has not been initialized yet
        if (mContext == null) {
            mContext = ctxt;
            base = mContext.getCacheDir().getAbsolutePath();
            startAsync();
        }
        verifyCache();
    }

    /**
     * Verifies the integrity of the data cache.
     * If the cache does not exist, then start the async to recreate it.
     */
    public void verifyCache() {
        // No need to verify the cache if the async is already running
        if (async == null || async.isCancelled() ||
                async.getStatus() == AsyncTask.Status.FINISHED) {
            File fStart = new File(base + File.separator + FILE_FLAG_START);
            if (!fStart.exists()) {
                // If our flag file does not exist, that signals that the cache does not exist
                // Therefore, the async should be started to recreate the cache
                startAsync();
            }
        }
    }

    /**
     * Checks for network access and garden wall before running async
     */
    private void startAsync() {
        new PingGoogleTask().execute();
    }

    /**
     * Prepares a list of hashmaps containing the data for the about screen
     * This data is taken from the cache'd file
     * <p/>
     * Each Item in the list is a paragraph
     * Each Item may respond to the keys: KEYS.ABOUT_*
     *
     * @return List<HashMap<String, String>> : our data
     */
    public List<HashMap<String, String>> getAboutData() {
        final List<HashMap<String, String>> data = new ArrayList<>();

        try {
            // Get the cache'd file
            File f = new File(base + File.separator + FILE_ABOUT);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_ABOUT);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONObject obj = new JSONObject(input);
                    JSONArray paragraphs = obj.getJSONArray(Keys.ABOUT_SUPER);
                    for (int e = 0; e < paragraphs.length(); e++) {
                        JSONObject paragraph = (JSONObject) paragraphs.get(e);
                        HashMap<String, String> map = new HashMap<>();
                        if (paragraph.has(Keys.ABOUT_TITLE))
                            map.put(Keys.ABOUT_TITLE, paragraph.getString(Keys.ABOUT_TITLE));
                        if (paragraph.has(Keys.ABOUT_BODY))
                            map.put(Keys.ABOUT_BODY, paragraph.getString(Keys.ABOUT_BODY));
                        if (paragraph.has(Keys.ABOUT_HEADER))
                            map.put(Keys.ABOUT_HEADER, paragraph.getString(Keys.ABOUT_HEADER));
                        data.add(map);
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Prepares a list of hashmaps containing the data for each exec
     * This data is taken from the cache'd file
     * <p/>
     * Each Item in the list is an exec
     * Each Item will respond to the following keys: Keys.EXECS_*
     *
     * @return List<HashMap<String, String>> : our data
     */
    public List<HashMap<String, String>> getExecsData() {
        final List<HashMap<String, String>> data = new ArrayList<>();

        try {
            // Get the cache'd file
            File f = new File(base + File.separator + FILE_EXECS);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_EXECS);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONObject obj = new JSONObject(input);
                    JSONArray execs = obj.getJSONArray(Keys.EXECS_SUPER);
                    for (int e = 0; e < execs.length(); e++) {
                        JSONObject exec = (JSONObject) execs.get(e);
                        HashMap<String, String> profile = new HashMap<>();
                        profile.put(Keys.EXECS_NAME, exec.getString(Keys.EXECS_NAME));
                        profile.put(Keys.EXECS_POSITION, exec.getString(Keys.EXECS_POSITION));
                        profile.put(Keys.EXECS_EMAIL, exec.getString(Keys.EXECS_EMAIL));
                        profile.put(Keys.EXECS_PHONE, exec.getString(Keys.EXECS_PHONE));
                        profile.put(Keys.EXECS_BIO, exec.getString(Keys.EXECS_BIO));
                        profile.put(Keys.EXECS_IMAGE_PROFILE,
                                exec.getString(Keys.EXECS_IMAGE_PROFILE));
                        profile.put(Keys.EXECS_IMAGE_TALL, exec.getString(Keys.EXECS_IMAGE_TALL));

                        data.add(profile);
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Prepares a list of hashmaps containing the data for each event
     * This data is taken from the cache'd file
     * <p/>
     * Each Item in the list is an Event.
     *
     * @return List<HashMap<String, String>> : our data
     */
    public List<Event> getEventsData() {
        final List<Event> data = new ArrayList<>();

        try {
            // Get the cache'd file
            File f = new File(base + File.separator + FILE_EVENTS);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_EVENTS);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONObject obj = new JSONObject(input);

                    // Get a hashmap of exams, for use with the events
                    JSONArray exams = obj.getJSONArray(Keys.EVENTS_EXAMS_SUPER);
                    HashMap<String, HashMap<String, String>> examsMap = new HashMap<>();
                    for (int i = 0; i < exams.length(); i++) {
                        JSONObject exam = (JSONObject) exams.get(i);
                        HashMap<String, String> examMap = new HashMap<>();
                        examMap.put(Keys.EXAMS_NAME, exam.getString(Keys.EXECS_NAME));
                        examMap.put(Keys.EXAMS_EXAMLINK, exam.getString(Keys.EXAMS_EXAMLINK));
                        examMap.put(Keys.EXAMS_PIS, exam.getString(Keys.EXAMS_PIS));
                        // Add the object to the parent hashmap using the provided shortcut as a key
                        examsMap.put(exam.getString(Keys.EXAMS_SHORTCUT), examMap);
                    }

                    JSONArray categories = obj.getJSONArray(Keys.EVENTS_CATEGORIES_SUPER);
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject event = (JSONObject) categories.get(i);
                        Event e = new Event();
                        if (event.has(Keys.CATEGORIES_NAME))
                            e.setName(event.getString(Keys.CATEGORIES_NAME));
                        if (event.has(Keys.CATEGORIES_TYPE))
                            e.setFormat(event.getString(Keys.CATEGORIES_TYPE));
                        if (event.has(Keys.CATEGORIES_CODE))
                            e.setCode(event.getString(Keys.CATEGORIES_CODE));
                        if (event.has(Keys.CATEGORIES_CLUSTER))
                            e.setCluster(event.getString(Keys.CATEGORIES_CLUSTER));
                        else
                            e.setCluster(Event.CLUSTER.MULTI);
                        if (event.has(Keys.CATEGORIES_MEMBERS))
                            e.setMembers(event.getString(Keys.CATEGORIES_MEMBERS));
                        if (event.has(Keys.CATEGORIES_GUIDELINES))
                            e.setGuidelines(event.getString(Keys.CATEGORIES_GUIDELINES));
                        if (event.has(Keys.CATEGORIES_EXAM))
                            // Set the exam information for the event based on the exam hashmap
                            if (examsMap.containsKey(event.getString(Keys.CATEGORIES_EXAM))) {
                                HashMap<String, String> exam = examsMap
                                        .get(event.getString(Keys.CATEGORIES_EXAM));
                                e.setExamLink(exam.get(Keys.EXAMS_EXAMLINK));
                                e.setExamName(exam.get(Keys.EXAMS_NAME));
                                e.setExamPIs(exam.get(Keys.EXAMS_PIS));
                            }
                        if (event.has(Keys.CATEGORIES_PRESENTATION))
                            e.setPresentation(event.getString(Keys.CATEGORIES_PRESENTATION));
                        if (event.has(Keys.CATEGORIES_PREPTIME))
                            e.setPreptime(event.getString(Keys.CATEGORIES_PREPTIME));
                        if (event.has(Keys.CATEGORIES_WRITTEN))
                            e.setWritten(event.getString(Keys.CATEGORIES_WRITTEN));
                        if (event.has(Keys.CATEGORIES_SAMPLE))
                            e.setSample(event.getString(Keys.CATEGORIES_SAMPLE));
                        if (event.has(Keys.CATEGORIES_RUBRIC))
                            e.setRubric(event.getString(Keys.CATEGORIES_RUBRIC));
                        if (event.has(Keys.CATEGORIES_DESCRIPTION))
                            e.setDescription(event.getString(Keys.CATEGORIES_DESCRIPTION));
                        if (event.has(Keys.CATEGORIES_EXTRAS))
                            e.setExtras(event.getString(Keys.CATEGORIES_EXTRAS));
                        data.add(e);
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Prepares a list of hashmaps containing the data for each regional event
     * This data is taken from the cache'd file
     * <p/>
     * Each Item in the list is an exec
     * Each Item will respond to the following keys: Keys.REGIONALS_*
     *
     * @return List<HashMap<String, String>> : our data
     */
    public List<HashMap<String, String>> getRegionalsData() {
        final List<HashMap<String, String>> data = new ArrayList<>();

        try {
            // Get the cache'd file
            File f = new File(base + File.separator + FILE_REGIONALS);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_REGIONALS);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONObject obj = new JSONObject(input);

                    JSONArray regionals = obj.getJSONArray(Keys.REGIONALS_SUPER);
                    for (int i = 0; i < regionals.length(); i++) {
                        JSONObject regional = (JSONObject) regionals.get(i);
                        HashMap<String, String> map = new HashMap<>();
                        map.put(Keys.REGIONALS_NAME, regional.getString(Keys.REGIONALS_NAME));
                        map.put(Keys.REGIONALS_DATE, regional.getString(Keys.REGIONALS_DATE));
                        map.put(Keys.REGIONALS_LOCATION,
                                regional.getString(Keys.REGIONALS_LOCATION));
                        map.put(Keys.REGIONALS_ADDRESS, regional.getString(Keys.REGIONALS_ADDRESS));
                        map.put(Keys.REGIONALS_ABOUT, obj.getString(Keys.REGIONALS_ABOUT));
                        map.put(Keys.REGIONALS_MULTIPLECHOICE,
                                obj.getString(Keys.REGIONALS_MULTIPLECHOICE));
                        map.put(Keys.REGIONALS_DEADLINE, obj.getString(Keys.REGIONALS_DEADLINE));
                        map.put(Keys.REGIONALS_COST, obj.getString(Keys.REGIONALS_COST));
                        data.add(map);
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Get the profile image for a specific executive.
     *
     * @param exec : the hashmap of executive data
     * @return : bitmap image
     */
    public Bitmap getExecProfileImage(HashMap<String, String> exec) {
        final String fileName = exec.get(Keys.EXECS_NAME).replace(" ", "") + Keys
                .EXECS_IMAGE_PROFILE;
        File f = new File(base + File.separator + fileName);
        if (f.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(base + File.separator + fileName, options);
        }
        return null;
    }

    /**
     * Get the tall image for a specific executive
     *
     * @param exec : the hashmap of executive data
     * @return : bitmap image
     */
    public Bitmap getExecTallImage(HashMap<String, String> exec) {
        final String fileName = exec.get(Keys.EXECS_NAME).replace(" ", "") + Keys
                .EXECS_IMAGE_TALL;
        File f = new File(base + File.separator + fileName);
        if (f.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(base + File.separator + fileName, options);
        }
        return null;
    }

    /**
     * Get a list of 20 most recent tweets from the cache.
     *
     * @return List<Tweet>
     */
    public List<Tweet> getTweets() {
        final List<Tweet> data = new ArrayList<>();

        try {
            // Get the cached file
            File f = new File(base + File.separator + FILE_TWITTER);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_TWITTER);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONArray obj = new JSONArray(input);
                    for (int i = 0; i < 20; i++) {
                        Tweet tweet = new Tweet();
                        tweet.setContent(((JSONObject) obj.get(i)).getString("text").trim());
                        tweet.setTime(((JSONObject) obj.get(i)).getString("created_at").trim());
                        String id = ((JSONObject) obj.get(i)).getString("id_str");
                        String screenname = ((JSONObject) obj.get(i))
                                .getJSONObject("user").getString("screen_name");
                        tweet.setLink(screenname, id);
                        data.add(tweet);
                    }

                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Prepares a list of object pairs that contain both title and link.
     * This data is taken from the cache'd file.
     * <p/>
     * Each Item in the list is a Pair
     * Each Pair contains two strings: The first being the title, the second being a url
     *
     * @return List<HashMap<String, String>> : our data
     */
    public List<Pair<String, String>> getProvincialsLinks() {
        final List<Pair<String, String>> data = new ArrayList<>();

        try {
            // Get the cache'd file
            File f = new File(base + File.separator + FILE_PROVINCIALS);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_PROVINCIALS);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONObject obj = new JSONObject(input);
                    JSONArray links = obj.getJSONArray(Keys.PROVINCIALS_LINKS_SUPER);
                    for (int i = 0; i < links.length(); i++) {
                        JSONObject item = links.getJSONObject(i);
                        data.add(new Pair<>(
                                item.getString(Keys.PROVINCIALS_LINK_TITLE),
                                item.getString(Keys.PROVINCIALS_LINK)));
                    }

                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Prepares a hashmap containing the data for the provincials event.
     * This data is taken from the cache'd file
     * <p/>
     * The Item will respond to the following keys: Keys.PROVINCIALS_*
     *
     * @return List<HashMap<String, String>> : our data
     */
    public HashMap<String, String> getProvincialsAbout() {
        final HashMap<String, String> data = new HashMap<>();

        try {
            // Get the cache'd file
            File f = new File(base + File.separator + FILE_PROVINCIALS);
            // Check that the file actually exists
            if (f.exists() && !f.isDirectory()) {
                // Read from the file
                FileInputStream fis = new FileInputStream(base + File.separator + FILE_PROVINCIALS);
                String input = NetworkUtils.readIt(fis);
                // If the input actually exists and isn't null, parse it
                if (input != null && !input.isEmpty()) {
                    JSONObject obj = new JSONObject(input);
                    JSONObject about = obj.getJSONObject(Keys.PROVINCIALS_ABOUT_SUPER);
                    data.put(Keys.PROVINCIALS_DESCRIPTION,
                            about.getString(Keys.PROVINCIALS_DESCRIPTION));
                    data.put(Keys.PROVINCIALS_SUBDESCRIPTION,
                            about.getString(Keys.PROVINCIALS_SUBDESCRIPTION));
                    data.put(Keys.PROVINCIALS_DATE,
                            about.getString(Keys.PROVINCIALS_DATE));
                    data.put(Keys.PROVINCIALS_STARTDATE,
                            about.getString(Keys.PROVINCIALS_STARTDATE));
                    data.put(Keys.PROVINCIALS_ENDDATE,
                            about.getString(Keys.PROVINCIALS_ENDDATE));
                    data.put(Keys.PROVINCIALS_LOCATION,
                            about.getString(Keys.PROVINCIALS_LOCATION));
                    data.put(Keys.PROVINCIALS_ADDRESS,
                            about.getString(Keys.PROVINCIALS_ADDRESS));
                    data.put(Keys.PROVINCIALS_COST,
                            about.getString(Keys.PROVINCIALS_COST));
                    data.put(Keys.PROVINCIALS_COSTSUB,
                            about.getString(Keys.PROVINCIALS_COSTSUB));
                    data.put(Keys.PROVINCIALS_DEADLINE,
                            about.getString(Keys.PROVINCIALS_DEADLINE));
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * EVENT_BUS qualifiers for sending notifications around the app
     */
    public enum EVENTBUS_EVENT {
        ABOUT, EXECS, PROFILE_IMAGE, TALL_IMAGE, REGIONALS, PROVINCIALS, EVENTS, TWITTER,
        LINKS, CANCELLED
    }

    /**
     * The keys used for parsing the json files
     */
    public static class Keys {
        final public static String ABOUT_HEADER = "header";
        final public static String ABOUT_TITLE = "title";
        final public static String ABOUT_BODY = "body";
        final public static String EXECS_NAME = "name";
        final public static String EXECS_POSITION = "position";
        final public static String EXECS_EMAIL = "email";
        final public static String EXECS_PHONE = "phone";
        final public static String EXECS_BIO = "bio";
        final public static String EXECS_IMAGE_PROFILE = "profileimage";
        final public static String EXECS_IMAGE_TALL = "tallimage";
        final public static String REGIONALS_ABOUT = "about";
        final public static String REGIONALS_MULTIPLECHOICE = "multiplechoice";
        final public static String REGIONALS_DEADLINE = "deadline";
        final public static String REGIONALS_COST = "cost";
        final public static String REGIONALS_NAME = "name";
        final public static String REGIONALS_DATE = "date";
        final public static String REGIONALS_LOCATION = "location";
        final public static String REGIONALS_ADDRESS = "address";
        final public static String PROVINCIALS_DESCRIPTION = "description";
        final public static String PROVINCIALS_SUBDESCRIPTION = "subdescription";
        final public static String PROVINCIALS_DEADLINE = "deadline";
        final public static String PROVINCIALS_COST = "cost";
        final public static String PROVINCIALS_COSTSUB = "costsub";
        final public static String PROVINCIALS_DATE = "date";
        final public static String PROVINCIALS_STARTDATE = "startdate";
        final public static String PROVINCIALS_ENDDATE = "enddate";
        final public static String PROVINCIALS_LOCATION = "location";
        final public static String PROVINCIALS_ADDRESS = "address";
        final public static String PROVINCIALS_LINK = "link";
        final public static String PROVINCIALS_LINK_TITLE = "title";
        final public static String CATEGORIES_NAME = "name";
        final public static String CATEGORIES_TYPE = "type";
        final public static String CATEGORIES_CODE = "code";
        final public static String CATEGORIES_CLUSTER = "cluster";
        final public static String CATEGORIES_MEMBERS = "members";
        final public static String CATEGORIES_GUIDELINES = "guidelines";
        final public static String CATEGORIES_EXAM = "exam";
        final public static String CATEGORIES_PRESENTATION = "presentation";
        final public static String CATEGORIES_PREPTIME = "preptime";
        final public static String CATEGORIES_WRITTEN = "written";
        final public static String CATEGORIES_SAMPLE = "sample";
        final public static String CATEGORIES_RUBRIC = "rubric";
        final public static String CATEGORIES_DESCRIPTION = "description";
        final public static String CATEGORIES_EXTRAS = "extra";
        final public static String EXAMS_NAME = "name";
        final public static String EXAMS_SHORTCUT = "shortcut";
        final public static String EXAMS_EXAMLINK = "exam";
        final public static String EXAMS_PIS = "PIs";
        final protected static String ABOUT_SUPER = "paragraphs";
        final protected static String EXECS_SUPER = "execs";
        final protected static String REGIONALS_SUPER = "regionals";
        final protected static String EVENTS_CATEGORIES_SUPER = "categories";
        final protected static String EVENTS_EXAMS_SUPER = "exams";
        final protected static String PROVINCIALS_ABOUT_SUPER = "about";
        final protected static String PROVINCIALS_LINKS_SUPER = "links";
    }

    class CacheAsync extends AsyncTask {

        boolean CACHEEXISTS = false;

        public CacheAsync() {
            super();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                // First check for the cache, and then create the cache flag
                verifyCache();
                createCacheFlag();
                // Provincials data has the highest priority as the links are on the main screen
                genericCacheData(URL_PROVINCIALS, FILE_PROVINCIALS,
                        EVENTBUS_EVENT.PROVINCIALS, EVENTBUS_EVENT.LINKS);
                // The about fragment is the first on the About Screen, so it gets priority
                genericCacheData(URL_ABOUT, FILE_ABOUT, EVENTBUS_EVENT.ABOUT);
                // This data is all secondary information, so they happen in no particular order
                genericCacheData(URL_EVENTS, FILE_EVENTS, EVENTBUS_EVENT.EVENTS);
                cacheTwitter();
                genericCacheData(URL_REGIONALS, FILE_REGIONALS, EVENTBUS_EVENT.REGIONALS);
                genericCacheData(URL_EXECS, FILE_EXECS, EVENTBUS_EVENT.EXECS);
                // Exec data specifies links to the profile images, which are finally downloaded
                // This is to avoid a bottleneck in the other downloads
                parseExecData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Check's if the cache already exists.
         * If it does not exist and we have no network access,
         * then we will read the data from the temporary file
         */
        private void verifyCache() {
            File f = new File(base + File.separator + FILE_FLAG_START);
            if (f.exists()) {
                CACHEEXISTS = true;
            }
        }

        /**
         * We want to verify that our started flag is still there
         * If it is gone, that means that the cache has been cleared,
         * and we need to stop this process so it can be restarted
         */
        private void verifyAsyncStarted() {
            File f = new File(base + File.separator + FILE_FLAG_START);
            if (!f.exists()) {
                cancel(true);
            }
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
            EventBus.getDefault().post(EVENTBUS_EVENT.CANCELLED);
        }

        /**
         * A FLAG file for the start and end of the cache async
         * If this file exists, we know the cache is intact
         *
         * @throws IOException
         */
        public void createCacheFlag() throws IOException {
            FileOutputStream fos = new FileOutputStream(base + File.separator +
                    FILE_FLAG_START);
            fos.write("FLAG".getBytes());
            fos.flush();
            fos.close();
        }

        /**
         * Download the the specified file and cache it for future use.
         * Finally set off an event on the EVentBus to notify the app of success
         *
         * @param URL    {String} : The location of the file on the internet
         * @param file   {String} : The location of the default file on the device
         * @param events {EVENTBUS_EVENT ...} : The events to be fired on completion
         * @throws IOException
         */
        private void genericCacheData(String URL, String file, EVENTBUS_EVENT... events) throws IOException {
            String data;
            InputStream inputStream = null;
            try {
                if (NETWORK_FLAG) {
                    // Update from web
                    URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    inputStream = conn.getInputStream();
                    data = NetworkUtils.readIt(inputStream);
                    Log.d("DECA", "Read data from web");
                } else if (!CACHEEXISTS) {
                    // We have no net access and no cache, so update from file
                    AssetManager am = mContext.getAssets();
                    inputStream = am.open(file);
                    data = NetworkUtils.readIt(inputStream);
                    Log.d("DECA", "Read data from file");
                } else {
                    return; // We have an existing cache, don't update
                }
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }

            // Cache the json file
            FileOutputStream fos = new FileOutputStream(base + File.separator + file);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
            // Fire off a notification to the event bus in order to let the app know to update its views
            for (EVENTBUS_EVENT event : events)
                EventBus.getDefault().post(event);
            verifyAsyncStarted();
        }

        /**
         * Using the already saved Exec data, the Exec images are cached
         *
         * @throws IOException
         */
        private void parseExecData() throws IOException {
            // Cache the exec images
            List<HashMap<String, String>> execData = getExecsData();

            // Profile images are the first seen so they are cached first
            for (HashMap<String, String> exec : execData) {
                cacheProfileImage(exec);
            }

            // Tall images are on each detail screen and larger, so are cached second
            for (HashMap<String, String> exec : execData) {
                cacheTallImage(exec);
            }
        }

        /**
         * Download the exec's profile image, pre transform it, then cache it
         *
         * @param profile : The exec profile
         * @throws IOException
         */
        private void cacheProfileImage(HashMap<String, String> profile) throws IOException {
            Bitmap bitmap = Picasso.with(mContext).load(profile.get(Keys
                    .EXECS_IMAGE_PROFILE))
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            int size = Math.min(source.getWidth(), source.getHeight());
                            Bitmap result;

                            // if the width is greater than the height, rotate the image 270
                            // degrees
                            if (source.getWidth() > size) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(270);
                                result = Bitmap.createBitmap(source, 0, 0,
                                        source.getWidth(), source.getHeight(), matrix,
                                        true);
                            } else {
                                result = source;
                            }

                            // Crop to a square located at the top of the image, to include faces
                            result = Bitmap.createBitmap(result, 0, 0, size, size);
                            if (result != source) {
                                source.recycle();
                            }

                            return result;
                        }

                        @Override
                        public String key() {
                            return "rotate()";
                        }
                    })
                    .get();

            final String execFileName =
                    profile.get(Keys.EXECS_NAME).replace(" ", "") + Keys.EXECS_IMAGE_PROFILE;
            try {
                if (bitmap != null) {
                    FileOutputStream fos =
                            new FileOutputStream(base + File.separator + execFileName);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    EventBus.getDefault().post(EVENTBUS_EVENT.PROFILE_IMAGE);
                    verifyAsyncStarted();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Download the exec's tall image, pre transform it, then cache it
         *
         * @param profile : The exec profile
         * @throws IOException
         */
        private void cacheTallImage(HashMap<String, String> profile) throws IOException {
            Bitmap bitmap = Picasso.with(mContext).load(profile.get(Keys.EXECS_IMAGE_TALL))
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            int size = Math.min(source.getWidth(), source.getHeight());
                            Bitmap result;

                            // if the width is greater than the height, rotate the image 270 degrees
                            if (source.getWidth() > size) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(270);
                                result = Bitmap.createBitmap(source, 0, 0,
                                        source.getWidth(), source.getHeight(), matrix,
                                        true);
                            } else
                                result = source;

                            if (result != source)
                                source.recycle();

                            return result;
                        }

                        @Override
                        public String key() {
                            return "rotate()";
                        }
                    })
                    .get();

            final String execFileName =
                    profile.get(Keys.EXECS_NAME).replace(" ", "") + Keys.EXECS_IMAGE_TALL;
            try {
                if (bitmap != null) {
                    FileOutputStream fos =
                            new FileOutputStream(base + File.separator + execFileName);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    EventBus.getDefault().post(EVENTBUS_EVENT.TALL_IMAGE);
                    verifyAsyncStarted();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Download a list of tweets from twitter using authentication, then cache it
         *
         * @throws IOException
         */
        private void cacheTwitter() throws IOException {
            final String oauthUrl = "https://api.twitter.com/oauth2/token";
            final String endPointUrl = "https://api.twitter.com/1.1/statuses/user_timeline" +
                    ".json?count=20&user_id=66115438&screen_name=DECAOntario";
            final String bearerToken = requestBearerToken(oauthUrl);

            HttpsURLConnection connection = null;
            try {
                URL url = new URL(endPointUrl);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(false);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Host", "api.twitter.com");
                connection.setRequestProperty("User-Agent", "DECA Ontario");
                connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
                connection.setUseCaches(false);

                connection.connect();

                // Parse the JSON response into a JSON mapped object to fetch fields from.
                String data = readResponse(connection);

                // Cache the json file
                FileOutputStream fos = new FileOutputStream(base + File.separator + FILE_TWITTER);
                fos.write(data.getBytes());
                fos.flush();
                fos.close();
                EventBus.getDefault().post(EVENTBUS_EVENT.TWITTER);
                verifyAsyncStarted();

            } catch (MalformedURLException e) {
                throw new IOException("Invalid endpoint URL specified.", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

            }
        }

        /**
         * Encodes the consumer key and secret to create the basic authorization key
         *
         * @param consumerKey    String
         * @param consumerSecret String
         * @return String : authorization key
         */
        private String encodeKeys(String consumerKey, String consumerSecret) {
            try {
                String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
                String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

                String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
                byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
                return new String(encodedBytes);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }

        /**
         * Constructs the request for requesting a bearer token and returns that token as a string
         *
         * @param endPointUrl String
         * @return String : bearer token
         * @throws IOException
         */
        private String requestBearerToken(String endPointUrl) throws IOException {
            HttpsURLConnection connection = null;
            String encodedCredentials = encodeKeys("ssId4sDUkYdnQb4MqCRsqt0n1",
                    "xhudf40QyvhoYGupJJ63byyLmViAKOCCRJyN0ZvyaPpj0lcFqu");

            try {
                URL url = new URL(endPointUrl);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(false);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Host", "api.twitter.com");
                connection.setRequestProperty("User-Agent", "DECA Ontario");
                connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;" +
                        "charset=UTF-8");
                connection.setRequestProperty("Content-Length", "29");
                connection.setUseCaches(false);

                connection.connect();

                writeRequest(connection, "grant_type=client_credentials");

                // Parse the JSON response into a JSON mapped object to fetch fields from.
                org.json.simple.JSONObject obj = (org.json.simple.JSONObject) JSONValue.parse
                        (readResponse(connection));

                if (obj != null) {
                    String tokenType = (String) obj.get("token_type");
                    String token = (String) obj.get("access_token");

                    return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
                }
                return new String();
            } catch (MalformedURLException e) {
                throw new IOException("Invalid endpoint URL specified.", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        /**
         * Reads a response for a given connection and returns it as a string.
         *
         * @param connection HttpsURLConnection
         * @return String : response
         * @throws IOException
         */
        private String readResponse(HttpsURLConnection connection) throws IOException {
            StringBuilder str = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection
                    .getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append(line + System.getProperty("line.separator"));
            }
            return str.toString().trim();
        }


        /**
         * Writes a request to a connection
         *
         * @param connection HttpsURLConnection
         * @param textBody   String
         * @return boolean : finished execution
         */
        private boolean writeRequest(HttpsURLConnection connection, String textBody) {
            try {
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection
                        .getOutputStream()));
                wr.write(textBody);
                wr.flush();
                wr.close();

                return true;
            } catch (IOException e) {
                return false;
            }
        }

    }

    /**
     * This AsyncTask's purpose is to ping Google and check if the device has internet access.
     * The main purpose is to not only test to see if the device is connect to the internet,
     * but also if it is in a walled garden situation, in which case no internet access
     * is reported.
     */
    class PingGoogleTask extends AsyncTask<Void, Void, Boolean> {

        private static final String mWalledGardenUrl = "http://clients3.google.com/generate_204";
        private static final int WALLED_GARDEN_SOCKET_TIMEOUT_MS = 10000;

        @Override
        protected Boolean doInBackground(Void... params) {
            return network();
        }

        private boolean network() {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mWalledGardenUrl); // "http://clients3.google.com/generate_204"
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setConnectTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
                urlConnection.setReadTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
                urlConnection.setUseCaches(false);
                urlConnection.getInputStream();
                // We got a valid response, but not from the real google
                return urlConnection.getResponseCode() != 204;
            } catch (IOException e) {
                Log.d("DECA", "Walled garden check - probably not a portal: exception " + e);
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Log.d("DECA", "Garden Walled");
            } else {
                NETWORK_FLAG = true;
                Log.d("DECA", "Network Access");
            }
            // Start the Cache Async Task after this check
            async = new CacheAsync().execute();
        }

    }
}