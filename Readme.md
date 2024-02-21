1) create a spring boot application with a controller

2) create an interceptor which will intercept all the requests and then invoke RateLimiterService to check if the request has to be rate limited

3) create an application-properties file in main/resources  which contains the redis properties

4) redis configuration bean 