# Counters Example app ⏱️  

For this exercise I decided to use the Single Activity principle, additionally Clean Architecture as a pattern through modules, MVVM, Jetapck (view bindings, nav component, list adapter) so in the project structure it looks as follows:
    - App: Everything related to the graphical interface such as Fragments, Activity, ViewModels, Adapters, ViewHolders
    - Domain: Everything related to business logic, data conversions and connection with the data source
    - Data: Data sources, endpoints

## Gradle

Dependencies are centralized inside the [dependencies.gradle](buildsystem) file in the `buildsystem` folder. This provides convenient centralization. It will allow us to update any dependency in a single place without the necessity to go manually to each gradle file where is used to change it, avoiding possible conflicts between versions if someone forgets to update one of these dependencies.

## Clean Architecture

The objective of [**Clean Architecture**](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) is the separation of concerns. Using a layers architecture like this one, allow us to be more independent of frameworks and limit proper boundaries. It also provide more benefits as higher testability and the possibility to scale well.

The project will start growing keeping in mind this, for now we don't have many modules but instead we'll handle the layer by using packages referencing them as such.

## Third Party libraries used
- Glide: Image loading and caching
- Retrofit: HTTP Client
- Retrofit Gson Converter: GSON converter for JSON responses

## Install and start the server

```
$ npm install
$ npm start
```
## Screenshots

| Splash Screen | Create Counter |
| --- | --- | 
|  ![Screenshot_1625108531](https://user-images.githubusercontent.com/7938140/124061982-849c9000-d9f5-11eb-9771-bc4d203fc80d.png) | ![Screenshot_1625108582](https://user-images.githubusercontent.com/7938140/124062094-c299b400-d9f5-11eb-8803-63814a169347.png)
| Examples | Errors |
| ![Screenshot_1625108585](https://user-images.githubusercontent.com/7938140/124062342-31770d00-d9f6-11eb-8b74-fd3f0eafb0e2.png) |  ![Screenshot_1625108516](https://user-images.githubusercontent.com/7938140/124062355-38058480-d9f6-11eb-9411-c95f42b0e13f.png) |
