# kguarder

> :guardsman: *A method based guarder for error-handling.*

Lite and simple make things easier and handy, lives in SpringBoot.
Working in normal method calls, api calls and transaction operations.

This project provides a declarative error-handling way based on method for SpringBoot applications.

- Method call return time limitation control
- Retry support
- Recovery support
- Custom method failure check

## Quick Start

```java
 JDK 11+
 SpringBoot v2.7+
```

### Import Dependency

> Current ${latest-version} :  `0.0.1-SNAPSHOT`.
```pom
    <dependency>
        <groupId>top.ko8e24</groupId>
        <artifactId>kguarder</artifactId>
        <version>${latest-version}</version>
    </dependency>
```

```gradle
implementation 'top.ko8e24.kguarder:kguarder:${latest-version}'
```

### Enable `kguarder`.

```java
// Active KGuarder
@EnableGuarder
@SpringBootApplication
public class KGuarderDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KGuarderDemoApplication.class, args);
    }

}
```

### Usage

```java
        @Guarder(
            // method cost limitation, if over time, interrupt and do retry
            timeout = 1L,
            timeoutUnit = TimeUnit.SECONDS,
            // custom check if the method call result failed or not
            failureCustomChecker = "myFailureCustomChecker",
            // except exceptions, throw out directly
            excludeEx = IllegalStateException.class,
            // include exceptions, default Exception.class
            includeEx = {UnsupportedOperationException.class, UnmodifiableClassException.class},
            // retry configs
            retry = @Retry(
                    // how much retry times need to do 
                    retryTimes = 3,
                    // delay to do retry
                    delay = 2L,
                    delayTimeUnit = TimeUnit.SECONDS,
                    // delay to do retry delay time strategy
                    delayStrategy = Retry.DelayStrategy.FIXED

            ),
            // recover
            recover = @Recover(
                    fallback = "myFallbacker"
            )

    )
    public Foo MyGuarderedMethodCall(String bar) {
            // ... save bar
            return new Foo(newBar);
            }
```

## Reminder

Since this is based on `SpringAOP`, it has the same limitation like `@Transactional` or `@Cacheable`.
The `Order` of `@Guarder` is `org.springframework.core.Ordered#LOWEST_PRECEDENCE-1`, which is higher priority than `@Transaction` by default.
We should keep transaction more close to method ideally.

Recommend to use `@Guarder` without other SpringAOP Annotations on same method together to avoid and potential `advice order` issue.
Make the method level sort instead of advices is better.

FYI, Those `@Enable**` configs are works as well, if you understand what I mean.

- `@EnableAspectJAutoProxy(exposeProxy = true)`
- `@EnableTransactionManagement(order = 1)`

## Build Project

```
JDK 11+
Gradle 7.2
```

## Contribution

- Folk this project and build locally.
- Make your best changes and raise PR.
- Using the `Issue` trace is a plus.


## License

MIT [@Koy](https://github.com/Koooooo-7)

