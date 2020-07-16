Original App Design Project 
===

# NextStreet

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that will allow users to send or receive local postal deliveries. Uber for local postal deliveries/shop deliveries (instead of sending a package or having to physically go there).

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Lifestyle, Shopping, Productivity 
- **Mobile:** App would contains phototaking capabilities so that users can take pictures of deliveries. It would also allow users to see profiles of qualified drivers and track the destination of their packages. 
- **Story:** This app allows at-risk individuals or those without cars to ship packages low-cost and quickly nearby. Even sending packages requires individuals to go to postal office, so this will allow those who are not able to leave their homes for any reason to send packages to others.
- **Market:** Users without easy transportation capabilities such as the elderly or ill. 
- **Habit:** Can be very often used for those who want/need to send letters or packages to others. May be used frequently by a few users, but more often used occasionally by most users.
- **Scope:** Will require location tracking for package, and access to a camera for pictures of the package. Requires a database of drivers to connect to.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Ability to log in and log out as a user.
* Ability to sign in as a new user, 
   - [x] using a custom form or 
    * through another account (such as the user's Facebook profile).
- [x] Ability to make a request to send package, by 
   - [x] taking a picture of the package contents, 
   - [x] writing a description of the package, 
   - [x] specifying ending destination with a long click on the map, and 
   - [ ] setting the starting destination to a location other than the current location
- [x] Ability to view package destination and starting location on a map.
- [ ] Ability to view current request details, such as driver name.

**Optional Nice-to-have Stories**

* Ability to choose to sign in as a driver or user.
* Ability to sign in as driver and accept/view a list of requests.
    * Ability to see past requests and details as a driver. 
* Write unit and integration tests for a few features.
* Implement Firebase as to update the Google maps based on traffic information (best estimate).
* See past package requests in a list from personal profile.
    * Card view of past package requests.
- [x] Ability to view personal profile. 
* Ability to view list of users to send package.
* Ability to view profile of driver, including driver photo.
* Set up cost of sending package.
* Material design graphics on buttons and map. 
* Ability to "track" package location (at least visually) on a map.
- [x] Map automatically zooms into current location.
- [x] Ability to search for package destination and starting location
- [ ] Ability to create a package request using a bottom sheet fragment.
* Implement notifications when package is delivered.


### 2. Screen Archetypes

* Setup Screen
    * (Optional) Ability to choose to sign in as a driver or user.
* Login Screen
   * Ability to log in and log out as a user.
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Sign Up Screen
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile). 
* Current Requests Screen
   * Ability to view current request. 
* New Requests Screen
   * Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
   * Ability to take and store pictures of the package contents.
   * (Optional)Ability to view list of available drivers.
   * (Optional) Ability to view profile of driver.
* (Optional) Available Drivers Screen
   * (Optional) Ability to view list of available drivers.
* Camera Screen
   *  Ability to take and store pictures of the package contents.
* Map Screen
   * Ability to view package destination and starting location on a map.
   * (Optional) Ability to "track" package location (at least visually) on a map.
* (Optional) Driver Profile Screen
   * Ability to view profile of driver.
* (Optional) Personal Profile Screen
   * Ability to view personal profile. 
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Current Requests
    * New Request
* Personal Profile
* Past Requests

**Flow Navigation** (Screen to Screen)
* Setup Screen 
    * "Login as Driver" Button to Driver Login Screen
    * "Login as User" Button to User Login Screen
* Login Screen (Plan to reuse a lot of screens between drivers and users)
   * "Login" Button to New Requests Screen
   * "Login through Facebook" Button to New Requests Screen
   * "Sign up" Button to Sign Up Screen
* Sign Up Screen
   * "Confirm" Button to Personal Profile Screen
* New Requests Screen
   * "Map" Button to Map Screen to view package destination
   * "Confirm" Button to confirm request
   * "Drivers" Button to view Available Drivers Screen
   * "Driver" Button to view Driver Profile Screen
* Current Requests Screen
   * "Driver" Button to view Driver Profile Screen
* Available Drivers Screen
   * Ability to view list of available drivers.
## Wireframes

<img src="https://i.imgur.com/NQ0dvfj.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

https://www.figma.com/file/a6SufM3D28xB95pvCcbWnp/NextStreet-New-Design?node-id=0%3A1

### [BONUS] Interactive Prototype
https://www.figma.com/proto/a6SufM3D28xB95pvCcbWnp/NextStreet-(New-Design)?node-id=1%3A364&scaling=min-zoom

## Schema 
### Models
#### Package Requests

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| user | Pointer   | Pointer to User who submitted request|
| driver | Pointer   | Pointer to Driver who fulfilling request|
| destination | GeoPoint (in singleton array) | Final destination of package|
| origin | GeoPoint   | Origin of package|
| image | ParseFile   | Optional image of package|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

#### User

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| username | String   | Username of user|
| firstName | String   | First name of user|
| lastName | String   | Last name of user|
| profilePic | ParseFile   | Optional profile picture of user|
| home | GeoPoint   | Optional "home" location of user|
| isDriver | Boolean   | Sets whether Driver or not. |
| rating | Number   | Optional rating of driver|
| currentLocation | GeoPoint   | Optional current location of driver|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|


### Networking
* Login Screen
   * (Read/GET) Log in as user
* Sign Up Screen
   * (Create/POST) Create new user profile
   * (Read/GET) Log in as user
* New Requests Screen
   * (Update/PUT) Set package details in Request
   * (Create/POST) Create new package request
* Current Requests Screen
   * (Read/GET) Query current Request for details.
* (Optional) Available Drivers Screen
   * (Read/GET) Query current available drivers
* Camera Screen
   * (Update/PUT) Set package image in Request.
* Map Screen
   * (Read/GET) Query current Request object to set destination and origin
   * (Read/GET) Query current Driver object to set current location
* (Optional) Driver Profile Screen
   * (Read/GET) Query logged in User object
* (Optional) Personal Profile Screen
   * (Read/GET) Query logged in User object
   * (Update/PUT) Update user profile image

#### Parse network request snippets
```
ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) { 
            if (e != null) {
                    
            } else {
                
            }
        });
```
```
ParseQuery<Request> query = ParseQuery.getQuery(Post.class);
        query.orderByDescending(KEY_CREATEDAT);
        query.include(KEY_USER);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Post>() {});
```

```
request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    
                } else {
                
                }
            }
        });
```
- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Will list Google Maps API by next week
===

# NextStreet

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that will allow users to send or receive local postal deliveries. Uber for local postal deliveries/shop deliveries (instead of sending a package or having to physically go there).

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Lifestyle, Shopping, Productivity 
- **Mobile:** App would contains phototaking capabilities so that users can take pictures of deliveries. It would also allow users to see profiles of qualified drivers and track the destination of their packages. 
- **Story:** This app allows at-risk individuals or those without cars to ship packages low-cost and quickly nearby. Even sending packages requires individuals to go to postal office, so this will allow those who are not able to leave their homes for any reason to send packages to others.
- **Market:** Users without easy transportation capabilities such as the elderly or ill. 
- **Habit:** Can be very often used for those who want/need to send letters or packages to others. May be used frequently by a few users, but more often used occasionally by most users.
- **Scope:** Will require location tracking for package, and access to a camera for pictures of the package. Requires a database of drivers to connect to.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Ability to log in and log out as a user.
* Ability to sign in as a new user, 
   - [x] using a custom form or 
    * through another account (such as the user's Facebook profile).
- [x] Ability to make a request to send package, by 
   - [x] taking a picture of the package contents, 
   - [x] writing a description of the package, 
   - [x] specifying ending destination with a long click on the map, and 
   - [ ] setting the starting destination to a location other than the current location
- [x] Ability to view package destination and starting location on a map.
- [ ] Ability to view current request details, such as driver name.

**Optional Nice-to-have Stories**

* Ability to choose to sign in as a driver or user.
* Ability to sign in as driver and accept/view a list of requests.
    * Ability to see past requests and details as a driver. 
* Write unit and integration tests for a few features.
* Implement Firebase as to update the Google maps based on traffic information (best estimate).
* See past package requests in a list from personal profile.
    * Card view of past package requests.
- [x] Ability to view personal profile. 
* Ability to view list of users to send package.
* Ability to view profile of driver, including driver photo.
* Set up cost of sending package.
* Material design graphics on buttons and map. 
* Ability to "track" package location (at least visually) on a map.
- [x] Map automatically zooms into current location.
- [x] Ability to search for package destination and starting location
- [ ] Ability to create a package request using a bottom sheet fragment.
* Implement notifications when package is delivered.


### 2. Screen Archetypes

* Setup Screen
    * (Optional) Ability to choose to sign in as a driver or user.
* Login Screen
   * Ability to log in and log out as a user.
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Sign Up Screen
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile). 
* Current Requests Screen
   * Ability to view current request. 
* New Requests Screen
   * Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
   * Ability to take and store pictures of the package contents.
   * (Optional)Ability to view list of available drivers.
   * (Optional) Ability to view profile of driver.
* (Optional) Available Drivers Screen
   * (Optional) Ability to view list of available drivers.
* Camera Screen
   *  Ability to take and store pictures of the package contents.
* Map Screen
   * Ability to view package destination and starting location on a map.
   * (Optional) Ability to "track" package location (at least visually) on a map.
* (Optional) Driver Profile Screen
   * Ability to view profile of driver.
* (Optional) Personal Profile Screen
   * Ability to view personal profile. 
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Current Requests
    * New Request
* Personal Profile
* Past Requests

**Flow Navigation** (Screen to Screen)
* Setup Screen 
    * "Login as Driver" Button to Driver Login Screen
    * "Login as User" Button to User Login Screen
* Login Screen (Plan to reuse a lot of screens between drivers and users)
   * "Login" Button to New Requests Screen
   * "Login through Facebook" Button to New Requests Screen
   * "Sign up" Button to Sign Up Screen
* Sign Up Screen
   * "Confirm" Button to Personal Profile Screen
* New Requests Screen
   * "Map" Button to Map Screen to view package destination
   * "Confirm" Button to confirm request
   * "Drivers" Button to view Available Drivers Screen
   * "Driver" Button to view Driver Profile Screen
* Current Requests Screen
   * "Driver" Button to view Driver Profile Screen
* Available Drivers Screen
   * Ability to view list of available drivers.
## Wireframes

<img src="https://i.imgur.com/BHJD33H.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

https://www.figma.com/file/a6SufM3D28xB95pvCcbWnp/NextStreet-New-Design?node-id=0%3A1

### [BONUS] Interactive Prototype
https://www.figma.com/proto/a6SufM3D28xB95pvCcbWnp/NextStreet-(New-Design)?node-id=1%3A364&scaling=min-zoom

## Schema 
### Models
#### Package Requests

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| user | Pointer   | Pointer to User who submitted request|
| driver | Pointer   | Pointer to Driver who fulfilling request|
| destination | GeoPoint (in singleton array) | Final destination of package|
| origin | GeoPoint   | Origin of package|
| image | ParseFile   | Optional image of package|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

#### User

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| username | String   | Username of user|
| firstName | String   | First name of user|
| lastName | String   | Last name of user|
| profilePic | ParseFile   | Optional profile picture of user|
| home | GeoPoint   | Optional "home" location of user|
| isDriver | Boolean   | Sets whether Driver or not. |
| rating | Number   | Optional rating of driver|
| currentLocation | GeoPoint   | Optional current location of driver|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|


### Networking
* Login Screen
   * (Read/GET) Log in as user
* Sign Up Screen
   * (Create/POST) Create new user profile
   * (Read/GET) Log in as user
* New Requests Screen
   * (Update/PUT) Set package details in Request
   * (Create/POST) Create new package request
* Current Requests Screen
   * (Read/GET) Query current Request for details.
* (Optional) Available Drivers Screen
   * (Read/GET) Query current available drivers
* Camera Screen
   * (Update/PUT) Set package image in Request.
* Map Screen
   * (Read/GET) Query current Request object to set destination and origin
   * (Read/GET) Query current Driver object to set current location
* (Optional) Driver Profile Screen
   * (Read/GET) Query logged in User object
* (Optional) Personal Profile Screen
   * (Read/GET) Query logged in User object
   * (Update/PUT) Update user profile image

#### Parse network request snippets
```
ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) { 
            if (e != null) {
                    
            } else {
                
            }
        });
```
```
ParseQuery<Request> query = ParseQuery.getQuery(Post.class);
        query.orderByDescending(KEY_CREATEDAT);
        query.include(KEY_USER);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Post>() {});
```

```
request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    
                } else {
                
                }
            }
        });
```
- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Will list Google Maps API by next week
===

# NextStreet

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that will allow users to send or receive local postal deliveries. Uber for local postal deliveries/shop deliveries (instead of sending a package or having to physically go there).

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Lifestyle, Shopping, Productivity 
- **Mobile:** App would contains phototaking capabilities so that users can take pictures of deliveries. It would also allow users to see profiles of qualified drivers and track the destination of their packages. 
- **Story:** This app allows at-risk individuals or those without cars to ship packages low-cost and quickly nearby. Even sending packages requires individuals to go to postal office, so this will allow those who are not able to leave their homes for any reason to send packages to others.
- **Market:** Users without easy transportation capabilities such as the elderly or ill. 
- **Habit:** Can be very often used for those who want/need to send letters or packages to others. May be used frequently by a few users, but more often used occasionally by most users.
- **Scope:** Will require location tracking for package, and access to a camera for pictures of the package. Requires a database of drivers to connect to.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Ability to log in and log out as a user.
* Ability to sign in as a new user, 
   - [x] using a custom form or 
    * through another account (such as the user's Facebook profile).
* Ability to make a request to send package, by 
   - [x] taking a picture of the package contents, 
   - [x] writing a description of the package, and 
    * specifying starting and ending destination.
* Ability to view package destination and starting location on a map.
* Ability to view current request details, such as driver name.
* Ability to sign in as driver and accept/view a list of requests.

**Optional Nice-to-have Stories**

* Implement Firebase as to update the Google maps based on traffic information (best estimate).
* See past package requests in a list from personal profile.
    * Card view of past package requests.
* Ability to view personal profile. 
* Ability to view list of users to send package.
* Ability to view profile of driver, including driver photo.
* Set up cost of sending package.
* Material design graphics on buttons and map. 
* Ability to "track" package location (at least visually) on a map.
* Implement notifications when package is delivered.
* Ability to see past requests and details as a driver. 

### 2. Screen Archetypes

* Login Screen
   * Ability to log in and log out as a user.
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Sign Up Screen
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile). 
* Current Requests Screen
   * Ability to view current request. 
* Package Requests Screen
   * Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
   * Ability to take and store pictures of the package contents.
   * (Optional)Ability to view list of available drivers.
   * (Optional) Ability to view profile of driver.
* (Optional) Available Drivers Screen
   * (Optional) Ability to view list of available drivers.
* Camera Screen
   *  Ability to take and store pictures of the package contents.
* Map Screen
   * Ability to view package destination and starting location on a map.
   * (Optional) Ability to "track" package location (at least visually) on a map.
* (Optional) Driver Profile Screen
   * Ability to view profile of driver.
* (Optional) Personal Profile Screen
   * Ability to view personal profile. 
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Package Requests
* Current Requests
* Personal Profile

**Flow Navigation** (Screen to Screen)

* Login Screen
   * "Login" Button to Package Requests Screen
   * "Login through Facebook" Button to Package Requests Screen
   * "Sign up" Button to Sign Up Screen
* Sign Up Screen
   * "Confirm" Button to Personal Profile Screen
* Package Requests Screen
   * "Map" Button to Map Screen to view package destination
   * "Confirm" Button to confirm request
   * "Drivers" Button to view Available Drivers Screen
   * "Driver" Button to view Driver Profile Screen
* Current Requests Screen
   * "Driver" Button to view Driver Profile Screen
* Available Drivers Screen
   * Ability to view list of available drivers.
## Wireframes

<img src="https://i.imgur.com/BHJD33H.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

https://www.figma.com/file/a6SufM3D28xB95pvCcbWnp/NextStreet-New-Design?node-id=0%3A1

### [BONUS] Interactive Prototype
https://www.figma.com/proto/a6SufM3D28xB95pvCcbWnp/NextStreet-(New-Design)?node-id=1%3A364&scaling=min-zoom

## Schema 
### Models
#### Package Requests

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| user | Pointer   | Pointer to User who submitted request|
| driver | Pointer   | Pointer to Driver who fulfilling request|
| destination | GeoPoint (in singleton array) | Final destination of package|
| origin | GeoPoint   | Origin of package|
| image | ParseFile   | Optional image of package|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

#### User

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| username | String   | Username of user|
| firstName | String   | First name of user|
| lastName | String   | Last name of user|
| profilePic | ParseFile   | Optional profile picture of user|
| home | GeoPoint   | Optional "home" location of user|
| isDriver | Boolean   | Sets whether Driver or not. |
| rating | Number   | Optional rating of driver|
| currentLocation | GeoPoint   | Optional current location of driver|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|


### Networking
* Login Screen
   * (Read/GET) Log in as user
* Sign Up Screen
   * (Create/POST) Create new user profile
   * (Read/GET) Log in as user
* Package Requests Screen
   * (Update/PUT) Set package details in Request
   * (Create/POST) Create new package request
* Current Requests Screen
   * (Read/GET) Query current Request for details.
* (Optional) Available Drivers Screen
   * (Read/GET) Query current available drivers
* Camera Screen
   * (Update/PUT) Set package image in Request.
* Map Screen
   * (Read/GET) Query current Request object to set destination and origin
   * (Read/GET) Query current Driver object to set current location
* (Optional) Driver Profile Screen
   * (Read/GET) Query logged in User object
* (Optional) Personal Profile Screen
   * (Read/GET) Query logged in User object
   * (Update/PUT) Update user profile image

#### Parse network request snippets
```
ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) { 
            if (e != null) {
                    
            } else {
                
            }
        });
```
```
ParseQuery<Request> query = ParseQuery.getQuery(Post.class);
        query.orderByDescending(KEY_CREATEDAT);
        query.include(KEY_USER);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Post>() {});
```

```
request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    
                } else {
                
                }
            }
        });
```
- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Will list Google Maps API sometime. 
https://hackmd.io/HOQB0z9lQzySDk3tl2gHpw

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that will allow users to send or receive local postal deliveries. Uber for local postal deliveries/shop deliveries (instead of sending a package or having to physically go there).

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Lifestyle, Shopping, Productivity 
- **Mobile:** App would contains phototaking capabilities so that users can take pictures of deliveries. It would also allow users to see profiles of qualified drivers and track the destination of their packages. 
- **Story:** This app allows at-risk individuals or those without cars to ship packages low-cost and quickly nearby. Even sending packages requires individuals to go to postal office, so this will allow those who are not able to leave their homes for any reason to send packages to others.
- **Market:** Users without easy transportation capabilities such as the elderly or ill. 
- **Habit:** Can be very often used for those who want/need to send letters or packages to others. May be used frequently by a few users, but more often used occasionally by most users.
- **Scope:** Will require location tracking for package, and access to a camera for pictures of the package. Requires a database of drivers to connect to.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Ability to log in and log out as a user.
* Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
* Ability to take and store pictures of the package contents.
* Ability to view package destination and starting location on a map.
* Ability to view current request details, such as driver name.
* Ability to sign in as driver and accept/view requests.

**Optional Nice-to-have Stories**

* Implement Firebase as to update the Google maps based on traffic information (best estimate).
* See past package requests in a list from personal profile.
    * Card view of past package requests.
* Ability to view personal profile. 
* Ability to view list of users to send package.
* Ability to view profile of driver, including driver photo.
* Set up cost of sending package.
* Set up an embedded map. 
* Material design graphics on buttons and map. 
* Ability to "track" package location (at least visually) on a map.

### 2. Screen Archetypes

* Login Screen
   * Ability to log in and log out as a user.
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Sign Up Screen
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile). 
* Current Requests Screen
   * Ability to view current request. 
* Package Requests Screen
   * Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
   * Ability to take and store pictures of the package contents.
   * (Optional)Ability to view list of available drivers.
   * (Optional) Ability to view profile of driver.
* (Optional) Available Drivers Screen
   * (Optional) Ability to view list of available drivers.
* Camera Screen
   *  Ability to take and store pictures of the package contents.
* Map Screen
   * Ability to view package destination and starting location on a map.
   * (Optional) Ability to "track" package location (at least visually) on a map.
* (Optional) Driver Profile Screen
   * Ability to view profile of driver.
* (Optional) Personal Profile Screen
   * Ability to view personal profile. 
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Package Requests
* Current Requests
* Personal Profile

**Flow Navigation** (Screen to Screen)

* Login Screen
   * "Login" Button to Package Requests Screen
   * "Login through Facebook" Button to Package Requests Screen
   * "Sign up" Button to Sign Up Screen
* Sign Up Screen
   * "Confirm" Button to Personal Profile Screen
* Package Requests Screen
   * "Map" Button to Map Screen to view package destination
   * "Confirm" Button to confirm request
   * "Drivers" Button to view Available Drivers Screen
   * "Driver" Button to view Driver Profile Screen
* Current Requests Screen
   * "Driver" Button to view Driver Profile Screen
* Available Drivers Screen
   * Ability to view list of available drivers.
## Wireframes

<img src="https://i.imgur.com/J622tQ9.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

https://www.figma.com/file/kiPvhX2u1JPHdBgKz4JTcC/FBU-Project-Package-Sending?node-id=0%3A1

### [BONUS] Interactive Prototype
https://www.figma.com/proto/kiPvhX2u1JPHdBgKz4JTcC/FBU-Project%3A-Package-Sending?node-id=1%3A4&scaling=scale-down

## Schema 
### Models
#### Package Requests

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| user | Pointer   | Pointer to User who submitted request|
| driver | Pointer   | Pointer to Driver who fulfilling request|
| destination | GeoPoint   | Final destination of package|
| origin | GeoPoint   | Origin of package|
| image | ParseFile   | Optional image of package|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

#### User

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| username | String   | Username of user|
| firstName | String   | First name of user|
| lastName | String   | Last name of user|
| profilePic | ParseFile   | Optional profile picture of user|
| home | GeoPoint   | Optional "home" location of user|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

#### Driver

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| username | String   | Username of driver|
| firstName | String   | First name of driver|
| lastName | String   | Last name of driver|
| profilePic | ParseFile   | Optional profile picture of driver|
| rating | Number   | Optional rating of driver|
| currentLocation | GeoPoint   | Optional current location of driver|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

### Networking
* Login Screen
   * (Read/GET) Log in as user
* Sign Up Screen
   * (Create/POST) Create new user profile
   * (Read/GET) Log in as user
* Package Requests Screen
   * (Update/PUT) Set package details in Request
   * (Create/POST) Create new package request
* Current Requests Screen
   * (Read/GET) Query current Request for details.
* (Optional) Available Drivers Screen
   * (Read/GET) Query current available drivers
* Camera Screen
   * (Update/PUT) Set package image in Request.
* Map Screen
   * (Read/GET) Query current Request object to set destination and origin
   * (Read/GET) Query current Driver object to set current location
* (Optional) Driver Profile Screen
   * (Read/GET) Query logged in User object
* (Optional) Personal Profile Screen
   * (Read/GET) Query logged in User object
   * (Update/PUT) Update user profile image

#### Parse network request snippets
```
ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) { 
            if (e != null) {
                    
            } else {
                
            }
        });
```
```
ParseQuery<Request> query = ParseQuery.getQuery(Post.class);
        query.orderByDescending(KEY_CREATEDAT);
        query.include(KEY_USER);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Post>() {});
```

```
request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    
                } else {
                
                }
            }
        });
```
- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Will list Google Maps API by next weekOriginal App Design Project 
===

# NextStreet

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that will allow users to send or receive local postal deliveries. Uber for local postal deliveries/shop deliveries (instead of sending a package or having to physically go there).

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Lifestyle, Shopping, Productivity 
- **Mobile:** App would contains phototaking capabilities so that users can take pictures of deliveries. It would also allow users to see profiles of qualified drivers and track the destination of their packages. 
- **Story:** This app allows at-risk individuals or those without cars to ship packages low-cost and quickly nearby. Even sending packages requires individuals to go to postal office, so this will allow those who are not able to leave their homes for any reason to send packages to others.
- **Market:** Users without easy transportation capabilities such as the elderly or ill. 
- **Habit:** Can be very often used for those who want/need to send letters or packages to others. May be used frequently by a few users, but more often used occasionally by most users.
- **Scope:** Will require location tracking for package, and access to a camera for pictures of the package. Requires a database of drivers to connect to.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Ability to log in and log out as a user.
* Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
* Ability to take and store pictures of the package contents.
* Ability to view package destination and starting location on a map.
* Ability to view current request details, such as driver name.
* Ability to sign in as driver and accept/view requests.

**Optional Nice-to-have Stories**

* Implement Firebase as to update the Google maps based on traffic information (best estimate).
* See past package requests in a list from personal profile.
    * Card view of past package requests.
* Ability to view personal profile. 
* Ability to view list of users to send package.
* Ability to view profile of driver, including driver photo.
* Set up cost of sending package.
* Set up an embedded map. 
* Material design graphics on buttons and map. 
* Ability to "track" package location (at least visually) on a map.

### 2. Screen Archetypes

* Login Screen
   * Ability to log in and log out as a user.
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile).
* Sign Up Screen
   * Ability to sign in as a new user, using a custom form or through another account (such as the user's Facebook profile). 
* Current Requests Screen
   * Ability to view current request. 
* Package Requests Screen
   * Ability to make a request to send package, by taking a picture of the package contents, writing a description of the package, and specifying starting and ending destination.
   * Ability to take and store pictures of the package contents.
   * (Optional)Ability to view list of available drivers.
   * (Optional) Ability to view profile of driver.
* (Optional) Available Drivers Screen
   * (Optional) Ability to view list of available drivers.
* Camera Screen
   *  Ability to take and store pictures of the package contents.
* Map Screen
   * Ability to view package destination and starting location on a map.
   * (Optional) Ability to "track" package location (at least visually) on a map.
* (Optional) Driver Profile Screen
   * Ability to view profile of driver.
* (Optional) Personal Profile Screen
   * Ability to view personal profile. 
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Package Requests
* Current Requests
* Personal Profile

**Flow Navigation** (Screen to Screen)

* Login Screen
   * "Login" Button to Package Requests Screen
   * "Login through Facebook" Button to Package Requests Screen
   * "Sign up" Button to Sign Up Screen
* Sign Up Screen
   * "Confirm" Button to Personal Profile Screen
* Package Requests Screen
   * "Map" Button to Map Screen to view package destination
   * "Confirm" Button to confirm request
   * "Drivers" Button to view Available Drivers Screen
   * "Driver" Button to view Driver Profile Screen
* Current Requests Screen
   * "Driver" Button to view Driver Profile Screen
* Available Drivers Screen
   * Ability to view list of available drivers.
## Wireframes

<img src="https://i.imgur.com/J622tQ9.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

https://www.figma.com/file/kiPvhX2u1JPHdBgKz4JTcC/FBU-Project-Package-Sending?node-id=0%3A1

### [BONUS] Interactive Prototype
https://www.figma.com/proto/kiPvhX2u1JPHdBgKz4JTcC/FBU-Project%3A-Package-Sending?node-id=1%3A4&scaling=scale-down

## Schema 
### Models
#### Package Requests

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| user | Pointer   | Pointer to User who submitted request|
| driver | Pointer   | Pointer to Driver who fulfilling request|
| destination | GeoPoint   | Final destination of package|
| origin | GeoPoint   | Origin of package|
| image | ParseFile   | Optional image of package|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

#### User

| Property | Type	  | Description |
| -------- | -------- | -------- |
| objectId | String   | Unique id for the package request (default field) |
| username | String   | Username of user|
| firstName | String   | First name of user|
| lastName | String   | Last name of user|
| profilePic | ParseFile   | Optional profile picture of user|
| home | GeoPoint   | Optional "home" location of user|
| isDriver | Boolean   | Sets whether Driver or not. |
| rating | Number   | Optional rating of driver|
| currentLocation | GeoPoint   | Optional current location of driver|
| createdAt | DateTime   | Date when request is created (default field)|
| updatedAt | DateTime   | Date when request is last updated (default field)|

### Networking
* Login Screen
   * (Read/GET) Log in as user
* Sign Up Screen
   * (Create/POST) Create new user profile
   * (Read/GET) Log in as user
* Package Requests Screen
   * (Update/PUT) Set package details in Request
   * (Create/POST) Create new package request
* Current Requests Screen
   * (Read/GET) Query current Request for details.
* (Optional) Available Drivers Screen
   * (Read/GET) Query current available drivers
* Camera Screen
   * (Update/PUT) Set package image in Request.
* Map Screen
   * (Read/GET) Query current Request object to set destination and origin
   * (Read/GET) Query current Driver object to set current location
* (Optional) Driver Profile Screen
   * (Read/GET) Query logged in User object
* (Optional) Personal Profile Screen
   * (Read/GET) Query logged in User object
   * (Update/PUT) Update user profile image

#### Parse network request snippets
```
ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) { 
            if (e != null) {
                    
            } else {
                
            }
        });
```
```
ParseQuery<Request> query = ParseQuery.getQuery(Post.class);
        query.orderByDescending(KEY_CREATEDAT);
        query.include(KEY_USER);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Post>() {});
```

```
request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    
                } else {
                
                }
            }
        });
```
- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Will list Google Maps API by next week
