package top.ko8e24.kguarder.core.tests.transaction.repository;

import org.springframework.data.repository.CrudRepository;
import top.ko8e24.kguarder.core.tests.transaction.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
