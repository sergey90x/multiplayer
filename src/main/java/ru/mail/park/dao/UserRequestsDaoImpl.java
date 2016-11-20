package ru.mail.park.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.*;

import org.springframework.stereotype.Repository;

import ru.mail.park.model.UserProfile;

@Repository
public class UserRequestsDaoImpl implements UserRequestsDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserProfile getUserById(Long id) {
        return entityManager.find(UserProfile.class, id);
    }

    @Override
    public Long addNewUser(String login, String name, String password, String email) {

        final UserProfile user = new UserProfile(login, name, email, password);
        entityManager.persist(user);

        return user.getId();
    }

    @Override
    public UserProfile duplicateEmail(String email) {

        UserProfile user = null;
        try {
            user = entityManager
                    .createQuery("select u from UserProfile u"
                            + " where u.email=:email", UserProfile.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            final Logger log = Logger.getLogger(UserRequestsDaoImpl.class.getName());
            log.log(Level.INFO, "NoResultException in duplicateEmail");
        }

        return user;
    }

    @Override
    public UserProfile existingUserByLogin(String login) {
        UserProfile user = null;
        try {
            user = entityManager
                    .createQuery("select u from UserProfile u"
                            + " where u.login =:login", UserProfile.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (NoResultException nre) {
            final Logger log = Logger.getLogger(UserRequestsDaoImpl.class.getName());
            log.log(Level.INFO, "NoResultExeception in existingUserByLogin");
        }
        return user;
    }

    @Override
    public List<UserProfile> getAllUsers() {

        List<UserProfile> users = null;
        // TODO надо как-то ограничить результат и никогда не доставать всех
        try {
            final Query query = entityManager
                    .createQuery("select u from UserProfile u");
            users = query.setMaxResults(50).getResultList();
        } catch (NoResultException nre) {
            final Logger log = Logger.getLogger(UserRequestsDaoImpl.class.getName());
            log.log(Level.INFO, "NoResultException in getAllUsers");
        }
        return users;
    }

    @Override
    public Boolean removeUser(Long id) {
        UserProfile entity = entityManager.find(UserProfile.class, id);
        if (entity == null) {
            return false;
        } else {
            entityManager.remove(entity);
            return true;
        }

    }

}
