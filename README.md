# DECA Ontario Android App

### [DECA Ontario](http://2016.deca.ca/)
DECA is an international youth business organization, with the Ontario association hosting over 12,000 high school students at 250+ schools. They host competitions that cater to different areas of business, including finance and marketing among others, in order to help students grow as leaders, grow in confidence, and network with the entrepreneurs and business professionals of the future.
I was personally a DECA member from grade 9 through 12, being chapter President in my last year. Not only did I create a brand new training program for the club, but I also excelled in competition, placing 3rd at the Provincial competition and 2nd at the International competition in Financial Consulting.

### The App
This DECA Ontario Android app was originally intended to be officially published by the organization, but the project lost steam and never reached final fruition on both Android and iOS platforms. It's original purpose was to be a semi-static app that would provide information to DECA Ontario members and links to resources pertaining to their events.
The following features were implemented:
* A home screen that links to the other subpages of the app
* Information is pulled from a JSON formatted file, and is pulled from a webserver and cached locally
* A social media feed that shows the most recent tweets from the DECA Ontario twitter account
* A list of the DECA Ontario executives, each with their own page
* A list of sortable events, each of which have their own information page and a unique identification bubble
* A calendar page which shows a WebView with a custom Google Calendar view
* A regionals screen, which allows the user to select their regional competition from a dropdown, and saves their preference
* A dynamic provincials dropdown bubble, which not only links to an about screen, but allows the dynamic loading of various web links.

### Technical Challenges
With every project I take on I always try to expand my working knowledge and implement features that I have never worked with before. Here is a breakdown of some of the technical challenges that I faced as I created this app.

##### Ease of editing (JSON)
* In order to allow the data to be easily edited from a remote location (server) by people with low amounts of technical experience, all information used in the app was chosen to be put in JSON format.
* A guide to JSON and explanation of every file was to be made, with the potential of a web interface to come in the future.
* This was important, because the data in the app would need to change at least every year as events, competitions, and the executive team change.

##### Background networking and file caching / dynamic reloading
* The main issue in this app was the need to balance offline usage with the ability to update the information in the app remotely.
* The first step in this solution was to have default JSON files saved within the app, for initial display.
* The next challenge was to download the new files from the web and cache them to the device. The cache is first created by copying the aforementioned default files. Then the app pulls the new files from the web and cache's them in place of the default files. This happens each time the app is loaded.
* Sometimes caches can be cleared, due to user choice or to memory restrictions, so the app is always checking for the integrity of the cache (through a cache flag file), and will reload if something is amiss.
* Since the app is downloading the files in the background, I needed a way to notify the various Activities to reload their data when the file was finished downloading. For this I used the EventBus library, to sent notifications to specific Activities.
* All of these actions were compiled in one DataSingleton class that managed the downloading, caching, parsing, and notifying in relation to JSON files.

### Timeframe
This app was worked on from late July 2015 to mid November 2015. Progress slowed down significantly because of school and extracurricular commitments, especially the fact that I was running my own school's DECA chapter at the same time.
