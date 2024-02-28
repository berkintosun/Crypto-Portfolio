# Crypto-Portfolio
Implementation of Cryptocurrency Portfolio Management System that is given as a task.

This project implements the given functionalities and provides a document about it's APIs by using `springdoc` and `OpenAPI3` schemas.
The documentation with swagger-ui is accessable via `/swagger-ui/index.html` endpoint. If you would like to try the APIs through swagger-ui, please use login API first as I did not setup embed auth to the swagger-ui

I used `CoinGecko` as 3rd party crypto API provider because I believe its interface is simpler and it provides better rate limit options in free tier.
I am caching the results for `1 minute` and this setting is changable through the configuration file `application.yml`
For this project, I also wanted to implement a custom authorization and authentication system. Currently user can signup, login and have role based authorities. In the implementation, it uses a default role as there are not that many functionalities that would help demonstrate benefits of it.

Flyway is used for database migration as well.

For functionalities, I choose to not aggregate the same type assets when user calls the `list` endpoint. The reason is that, every asset has their own purchase date and this information would be lost during the process. This data can be useful to display the potential profit that they achieved by comparing the asset market value at given time with current one.
For deployment, I would add Dockerfile, but I choose to skip it as I was coded on Windows without WSL.

For running `gradle bootRun`
For testing `gradle test` 

would be enough and gradle wrappers are also provided inside of this project. Please use Gradle version >=8.5 as there might be issues such as https://github.com/gradle/gradle/issues/24390

Also for mapping task, I was planning to use Mapstruct, but I avoided as it was small mappings.

I am grateful to anyone that takes time to check this project out. I appreciate any feedback and suggestions.