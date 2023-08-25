package top.ko8e24.kguarder.core.tests.transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import top.ko8e24.kguarder.core.configuration.GuarderConfiguration;
import top.ko8e24.kguarder.core.tests.transaction.annotation.CheckAfterTransaction;
import top.ko8e24.kguarder.core.tests.transaction.annotation.CheckBeforeTransaction;
import top.ko8e24.kguarder.core.tests.transaction.annotation.CheckTransactionTestExecutionListener;
import top.ko8e24.kguarder.core.tests.transaction.domain.User;
import top.ko8e24.kguarder.core.tests.transaction.repository.UserRepository;
import top.ko8e24.kguarder.core.tests.transaction.support.MockConfiguration;
import top.ko8e24.kguarder.core.tests.transaction.support.MockOperationService;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
@ContextConfiguration(classes = {GuarderConfiguration.class, MockConfiguration.class})
@TestExecutionListeners(listeners = {CheckTransactionTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@EnableJpaRepositories(basePackages = {"top.ko8e24.kguarder.core.tests.transaction.repository"})
@EntityScan("top.ko8e24.kguarder.core.tests.transaction.domain")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
//@EnableAspectJAutoProxy(exposeProxy = true)
public class KGGuarderTransactionTests {

    @SpyBean
    private MockOperationService operationService;
    @SpyBean
    private UserRepository userRepository;

    @Test
    public void shouldReturnUserWhenCallGetUserByIdGivenRetry() {
        final User user = operationService.getUserById(1L);
        verify(operationService, times(2)).getUserById(1L);
        Assertions.assertEquals(1L, user.getId());
    }

    @Test
    public void shouldSaveUserSuccessWhenCallCreateUserGivenNoRetry() {
        Assertions.assertDoesNotThrow(() -> operationService.createUserNormal(new User(null, "kguarder")));
        verify(userRepository, only()).save(any(User.class));
    }

    @Test
    @Tag("saveUser")
    public void shouldSaveUserSuccessWhenCallCreateUserGivenGuarderEx() {
        Assertions.assertThrows(IllegalStateException.class, () -> operationService.createUserWithGuarderEx(new User(null, "kguarder")));
    }

    @Test
    public void shouldSaveUserFailedWhenCallCreateUserGivenNoRetry() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> operationService.createUserNormalError(new User(null, "kguarder")));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @Tag("saveUser")
    public void shouldSaveUserWhenCallCreateUserGivenRetry() {
        final User user = operationService.createUser(new User(null, "kguarder"));
        verify(operationService, times(2)).createUser(any(User.class));
        Assertions.assertEquals("kguarder", user.getName());
        Assertions.assertNotNull(user.getId());
    }

    @Test
    @Tag("saveUserEx")
    public void shouldNotSaveUserWhenCallCreateUserGivenRetry() {
        Assertions.assertThrows(IllegalStateException.class, () -> operationService.createUserWithMatchedExcludeEx(new User(null, "kguarder")));
        verify(userRepository, never()).save(any(User.class));
    }

    @CheckBeforeTransaction(@Tag("saveUser"))
    public void checkBeforeSaveUser(Method testMethod) {
        final List<User> allUsers = operationService.findAllUsers();
        Assertions.assertEquals(4, allUsers.size());
    }

    @CheckAfterTransaction(@Tag("saveUser"))
    public void checkAfterSaveUser(Method testMethod) {
        System.out.println(testMethod.getName());
        final List<User> allUsers = operationService.findAllUsers();
        Assertions.assertEquals(5, allUsers.size());
    }

    @CheckBeforeTransaction(@Tag("saveUserEx"))
    public void checkBeforeSaveUserEx(Method testMethod) {
        final List<User> allUsers = operationService.findAllUsers();
        Assertions.assertEquals(4, allUsers.size());
    }

    @CheckAfterTransaction(@Tag("saveUserEx"))
    public void checkAfterSaveUserEx(Method testMethod) {
        final List<User> allUsers = operationService.findAllUsers();
        Assertions.assertEquals(4, allUsers.size());
    }

}
