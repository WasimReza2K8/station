# Charging Station
In this challenge I have developed an app that shows the available charging stations 5km near to a 
specific POI. To solve this challenge I have used MVI(Model, View, Intent)+MVVM 
architecture with clean architecture approach. The whole code base is divided into 5 modules app, 
core, feature-station, domain-station and data-station. The app module contains only the app related 
code and depends on the feature modules. 

The core module contains the code which is common for the whole project. If we extend any feature 
then we can reuse the code of core module. So the most common functionalities are placed into core 
module. For big project it can be further divided into more modules like core-ui, core-network but 
for this small MVP(Minimum Viable Product) all of them integrated into single core module. 

The data layer is responsible for providing the data from different sources. For this app this layer
will collect data from the backend server and provide them to domain module. So the data flow goes 
like data -> domain -> presentation.

The domain layer is responsible the business logic. The use case contains the business logic. The 
domain module is agnostic of any other module. It does not depend on any other module. In this 
feature use case just get the charging stations from repository and provide them to the presentation 
layer. For this MVP, this layer is doing nothing rather than providing data to the feature however,
in a real project this layer is very important as it contains all the business logic which can be 
reused to other features. The purpose of this layer for this MVP is to unify the architecture for 
better understandability and maintainability.

feature-station module contains the code of the presentation layer of the feature. We show the 
charging stations in this feature and now this is the only feature that app contains. The feature 
module depends on its domain and data module. It contains the ui, view model, ui data model , util 
and mapper classes. View model is dependent on use case for business logic. In MVI architecture, 
the user interactions are considered as events or intent which change the model and recreate 
immutable states which is shown in the view. 

The technologies and tools I have used are given below:
- kotlin
- Hilt
- Retrofit
- Kotlinx serialization
- Coroutines (flow)
- Jetpack
- JUnit
- mockk
- turbine
- chucker
- detekt
- ktlint
