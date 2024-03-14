## rate-limiter

Basic implementation of a rate limiter using Spring Boot and Redis

Key Points:

* Create a spring boot application with a controller which receives the request from client
* Then, create an interceptor which will intercept all the requests and then invoke RateLimiterService to check if the
  request has to be rate limited. The interceptor has to be registed with the WebMvcConfigurer
* The redis configuration is stored in a resources file and is injected into the RedisConfiguration Bean
* Currently, the app uses a crude form of token bucket algorithm, but could be extended to utilize other algorithms
* All the data stored in the Redis DB is in Strings, therefore, we are using String Serializer. In the future, as the
  models become more complex, we can utilize POJO's
