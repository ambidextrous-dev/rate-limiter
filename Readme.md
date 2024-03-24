## rate-limiter

Implementation of a configurable and flexible rate limiter application using Spring Boot and Redis

Key Points:
* Create a spring boot application with a controller which receives the request from client
* Then, create an interceptor which will intercept all the requests and then invoke RateLimiterService to check if the
  request has to be rate limited. The interceptor has to be registed with the WebMvcConfigurer
* The redis configuration is stored in a resources file and is injected into the RedisConfiguration Bean
* We can configure our choice of the rate limiter algorithm in the application.properties file
* In this case we have two endpoints, the greeting endpoint is not rate limited, while the bitcoin endpoint is rate limited
* All the data stored in the Redis DB is in Strings, therefore, we are using String Serializer. In the future, as the
  models become more complex, we can utilize POJO's

Supported Algorithms:
1. **Token Bucket (Bucket_Size, Refill_Rate)**: Basic algorithm, where we maintain a bucket in Redis, which gets refilled at a certain rate (for example: 3 tokens per minute). Each processed request consumes a token. If all tokens are consumed the request is blocked. Easy to implement,  but this can cause a surge of traffic for a short time, right around the time when the bucket is refilled. Another disadvantage of this approach is that it causes an inconsistent traffic pattern
2. **Leaky Bucket(Bucket_Size, Processing_Rate)**: Provides a fixed rate of processing. Here, we have a queue where all the requests are queued. The requests are processed in FIFO. If the queue is full, requests are blocked. On the benefits side it is easy to implement, has a low memory profile and provides a smoother and consistent traffic pattern for the server. However, its disadvantage is that it does not reflect real life scenarios, where the actual demand may vary based on time or some other external factor

