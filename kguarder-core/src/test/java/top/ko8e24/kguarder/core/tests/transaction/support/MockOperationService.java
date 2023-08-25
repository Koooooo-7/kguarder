package top.ko8e24.kguarder.core.tests.transaction.support;


import org.assertj.core.util.Lists;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.ko8e24.kguarder.core.annotation.Guarder;
import top.ko8e24.kguarder.core.annotation.Retry;
import top.ko8e24.kguarder.core.tests.transaction.domain.User;
import top.ko8e24.kguarder.core.tests.transaction.repository.UserRepository;

import java.util.List;


public class MockOperationService {

    private boolean getUserByIdFirstCall = true;
    private boolean saveUserByIdFirstCall = true;

    private boolean createUserNormalErrorFirstCall = true;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Guarder(
            retry = @Retry(
                    retryTimes = 3
            )
    )
    @Transactional
    public User getUserById(Long id) {
        if (getUserByIdFirstCall) {
            getUserByIdFirstCall = false;
            throw new IllegalStateException("Mock an Exception");
        }
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User createUserNormal(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User createUserNormalError(User user) {
        throw new IllegalStateException("Mock an Exception");
    }

    @Guarder(
            retry = @Retry(
                    retryTimes = 3
            )
    )
    @Transactional
    public User createUser(User user) {
        if (saveUserByIdFirstCall) {
            saveUserByIdFirstCall = false;
            throw new IllegalStateException("Mock an Exception");
        }
        return userRepository.save(user);
    }

    @Guarder(
            excludeEx = Exception.class,
            retry = @Retry(
                    retryTimes = 3
            )
    )
    @Transactional
    public User createUserWithMatchedExcludeEx(User user) {
        if (createUserNormalErrorFirstCall) {
            createUserNormalErrorFirstCall = false;
            throw new IllegalStateException("Mock an Exception");
        }

        return userRepository.save(user);

    }

    @Guarder(
            failureCustomChecker = "mockThrowCustomFailureChecker",
            retry = @Retry(
                    retryTimes = 1
            )
    )
    @Transactional
    public User createUserWithGuarderEx(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public List<User> findAllUsers() {
        return Lists.newArrayList(userRepository.findAll());
    }

}
