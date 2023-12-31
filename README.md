# kguarder

> :guardsman: *A method based guarder for error-handling.*


<p align="center">
    <img src="https://github.com/Koooooo-7/kguarder/actions/workflows/ci.yml/badge.svg"/>
&nbsp;
&nbsp;
<a href="https://codecov.io/gh/Koooooo-7/kguarder" rel="nofollow">
    <img src="https://codecov.io/gh/Koooooo-7/kguarder/graph/badge.svg?token=2EL1HG9J6B"/> 
</a>
</p>

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

> Current ${latest-version} :  `0.0.1`.
```pom
    <dependency>
        <groupId>top.ko8e24</groupId>
        <artifactId>kguarder</artifactId>
        <version>0.0.1</version>
    </dependency>
```

```gradle
implementation 'top.ko8e24.kguarder:kguarder:0.0.1'
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

Since it bases on `SpringAOP` support, which has the same limitation like `@Transactional` or `@Cacheable`.
The `Order` of `@Guarder` is `org.springframework.core.Ordered#LOWEST_PRECEDENCE-1` by default, which is higher priority than `@Transaction`.
Ideally, We should keep transaction more close to method.

Recommend to use `@Guarder` without other SpringAOP Annotations on same method together to avoid any potential `advice order` issue.
Make the sort on method construct level instead of advices is better.

FYI, those `@Enable**` configs are works as well, if you know what I mean.

- `@EnableAspectJAutoProxy(exposeProxy = true)`
- `@EnableTransactionManagement(order = 1)`
- `...`

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

