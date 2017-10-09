package by.it.milosh.service.serviceImpl;

import by.it.milosh.model.PhoneNumber;
import by.it.milosh.model.Tariff;
import by.it.milosh.model.User;
import by.it.milosh.repository.PhoneNumberRepository;
import by.it.milosh.repository.ServiceRepository;
import by.it.milosh.repository.TariffRepository;
import by.it.milosh.repository.UserRepository;
import by.it.milosh.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void addTariffToUser(User user, Long tariff_id) {
        Tariff tariff = tariffRepository.findOne(tariff_id);
        user.setTariff(tariff);
        userRepository.save(user);
    }

    @Override
    public void addNumberToUser(User user, PhoneNumber phoneNumber) {
        //user.setPhoneNumber(phoneNumber);
        //userRepository.save(user);
        phoneNumber.setUser(user);
        phoneNumber.setUsed(true);
        phoneNumberRepository.save(phoneNumber);
    }

    @Override
    public Long numberOfUsers() {
        return userRepository.numberOfUsers();
    }

    @Override
    public void addServiceToUser(Long user_id, Long service_id) {
        User user = userRepository.findOne(user_id);
        by.it.milosh.model.Service service = serviceRepository.findOne(service_id);
        user.getServices().add(service);
        userRepository.save(user);
    }

    @Override
    public List<by.it.milosh.model.Service> getServicesOfUser(Long user_id) {
        User user = userRepository.findOne(user_id);
        List<by.it.milosh.model.Service> services = user.getServices();
        return services;
    }

    @Override
    public List<by.it.milosh.model.Service> getServiceNonUser(Long user_id) {
        User user = userRepository.findOne(user_id);
        List<by.it.milosh.model.Service> servicesOfUser = user.getServices();
        List<by.it.milosh.model.Service> servicesNonUser = serviceRepository.findAll();
        servicesNonUser.removeAll(servicesOfUser);
        return servicesNonUser;
    }

}
