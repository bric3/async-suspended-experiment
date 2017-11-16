

# With sync endpoint

```sh
http :8000/poke wait==4 Accept:text/vnd.sync+plain --verbose --timeout 10
```

```
2017-11-16T15:18:24.851 --> filter : http-nio-8000-exec-1
2017-11-16T15:18:24.853 --> pokeSync : http-nio-8000-exec-1
2017-11-16T15:18:24.853 --> waitForIt : http-nio-8000-exec-1
2017-11-16T15:18:24.853 --> waitForIt : 15
2017-11-16T15:18:39.857 <-- waitForIt : http-nio-8000-exec-1
2017-11-16T15:18:39.858 <-- pokeSync : http-nio-8000-exec-1
2017-11-16T15:18:39.858 <-- filter : http-nio-8000-exec-1
2017-11-16T15:18:39.860 --> filter : http-nio-8000-exec-1
2017-11-16T15:18:39.861 --> pokeSync : http-nio-8000-exec-1
2017-11-16T15:18:39.861 --> waitForIt : http-nio-8000-exec-1
2017-11-16T15:18:39.861 --> waitForIt : 15
2017-11-16T15:18:54.866 <-- waitForIt : http-nio-8000-exec-1
2017-11-16T15:18:54.866 <-- pokeSync : http-nio-8000-exec-1
2017-11-16T15:18:54.867 <-- filter : http-nio-8000-exec-1
```

In a situation where no more thread are available, thread starving, new requests
cannot be handled as long as previous work is not terminated (to free a thread).

* Request 1 is holding on `http-nio-8000-exec-1` for 15 seconds
* Request 2 is queued in tomcat and is waiting for slot in the connector pool 
  (i.e a thread, in this case `http-nio-8000-exec-1`)
 

# With JAX-RS suspended response

```sh
http :8000/poke wait==4 Accept:text/vnd.suspended+plain --verbose --timeout 10
```

```
2017-11-16T15:21:03.856 --> filter : http-nio-8000-exec-1
2017-11-16T15:21:03.862 --> pokeSuspended : http-nio-8000-exec-1
2017-11-16T15:21:03.863 <-- pokeSuspended : http-nio-8000-exec-1
2017-11-16T15:21:03.863 --> pokeSuspended task : pool-1-thread-1
2017-11-16T15:21:03.863 --> waitForIt : pool-1-thread-1
2017-11-16T15:21:03.863 --> waitForIt : 15
2017-11-16T15:21:03.863 <-- filter : http-nio-8000-exec-1
2017-11-16T15:21:03.864 --> filter : http-nio-8000-exec-1
2017-11-16T15:21:03.866 --> pokeSuspended : http-nio-8000-exec-1
2017-11-16T15:21:03.866 <-- pokeSuspended : http-nio-8000-exec-1
2017-11-16T15:21:03.866 --> pokeSuspended task : pool-1-thread-2
2017-11-16T15:21:03.866 --> waitForIt : pool-1-thread-2
2017-11-16T15:21:03.866 <-- filter : http-nio-8000-exec-1
2017-11-16T15:21:03.866 --> waitForIt : 15
2017-11-16T15:21:18.866 <-- waitForIt : pool-1-thread-1
2017-11-16T15:21:18.867 <-- waitForIt : pool-1-thread-2
2017-11-16T15:21:18.869 <-- pokeSuspended task : pool-1-thread-1
2017-11-16T15:21:18.869 <-- pokeSuspended task : pool-1-thread-2
```

Note that connector thread returns soon, and is ready to handle 
another request. Work is done in the worker.


# With the async filter listener

```sh
http :8000/poke wait==4 Accept:text/vnd.suspended+plain --verbose --timeout 10
```


```
2017-11-16T17:15:24.782 --> filter : http-nio-8000-exec-1
2017-11-16T17:15:24.885 --> pokeSuspended : http-nio-8000-exec-1
2017-11-16T17:15:24.886 <-- pokeSuspended : http-nio-8000-exec-1
2017-11-16T17:15:24.886 --> pokeSuspended task : pool-1-thread-1
2017-11-16T17:15:24.887 --> waitForIt : pool-1-thread-1
2017-11-16T17:15:24.887 --> waitForIt : 4
2017-11-16T17:15:24.892 <-- filter : http-nio-8000-exec-1
2017-11-16T17:15:24.893 --> filter : http-nio-8000-exec-1
2017-11-16T17:15:24.895 --> pokeSuspended : http-nio-8000-exec-1
2017-11-16T17:15:24.895 <-- pokeSuspended : http-nio-8000-exec-1
2017-11-16T17:15:24.895 --> pokeSuspended task : pool-1-thread-2
2017-11-16T17:15:24.895 --> waitForIt : pool-1-thread-2
2017-11-16T17:15:24.895 --> waitForIt : 4
2017-11-16T17:15:24.895 <-- filter : http-nio-8000-exec-1
2017-11-16T17:15:28.892 <-- waitForIt : pool-1-thread-1
2017-11-16T17:15:28.899 <-- waitForIt : pool-1-thread-2
2017-11-16T17:15:28.912 <-- filter [complete] : http-nio-8000-exec-1
2017-11-16T17:15:28.913 <-- pokeSuspended task : pool-1-thread-1
2017-11-16T17:15:28.913 <-- pokeSuspended task : pool-1-thread-2
2017-11-16T17:15:28.917 <-- filter [complete] : http-nio-8000-exec-1
```

Note the `filter [complete] : http-nio-8000-exec-1` which indicate the async 
listener was executed on a thread of the connector (here the same one as the connector 
is configured to 1 thread only).