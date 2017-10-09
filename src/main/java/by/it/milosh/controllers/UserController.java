package by.it.milosh.controllers;

import by.it.milosh.model.*;
import by.it.milosh.service.service.PhoneNumberService;
import by.it.milosh.service.service.ServiceService;
import by.it.milosh.service.service.TariffService;
import by.it.milosh.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TariffService tariffService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private PhoneNumberService phoneNumberService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String personal(Principal principal, Model model) {
        User user = userService.findUserByUsername(principal.getName());
        int numberOfTariffs = tariffService.findAll().size();
        model.addAttribute("numberOfTariffs", numberOfTariffs);
        model.addAttribute("user", user);

        return "user/user";
    }

    @RequestMapping(value = "/connect", method = RequestMethod.GET)
    public String connect(Model model) {
        List<Tariff> tariffs = tariffService.findAll();
        model.addAttribute("tariffs", tariffs);
        model.addAttribute("tariff", new Tariff());
        return "user/connect";
    }

    /* было
    @RequestMapping(value = "/connect", params = {"connect"}, method = RequestMethod.POST)
    public String connect(@Valid @ModelAttribute("tariff") Tariff tariff, BindingResult br, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Long tar_id = tariff.getTariff_id();
        userService.addTariffToUser(user, tar_id);
        List<PhoneNumber> phoneNumbers = phoneNumberService.findAllUnusedNumbers();
        PhoneNumber pn = phoneNumbers.get(0);
        userService.addNumberToUser(user, pn);
        return "redirect:/user";
    }
    */

    @RequestMapping(value = "/connect", params = {"connect"}, method = RequestMethod.POST)
    public String connect(@Valid @ModelAttribute("tariff") Tariff tariff, BindingResult br, Principal principal, Model model) {
        User user = userService.findUserByUsername(principal.getName());
        Long tar_id = tariff.getTariff_id();
        userService.addTariffToUser(user, tar_id);
        Integer userAccount = user.getAccount();
        Tariff checkTariff = tariffService.getById(tar_id);
        Integer costPerMonth = checkTariff.getCostPerMonth();

        Integer newAccount = userAccount - costPerMonth;
        user.setAccount(newAccount);
        userService.save(user);

        List<PhoneNumber> phoneNumbers = phoneNumberService.findAllUnusedNumbers();
        PhoneNumber pn = phoneNumbers.get(0);
        userService.addNumberToUser(user, pn);

        model.addAttribute("tariff", checkTariff);
        model.addAttribute("userWrapper", new UserWrapper());
        return "user/connectAdvancePayment";
    }

    @RequestMapping(value = "/connectAdvancePayment", method = RequestMethod.POST)
    public String connectAdvancePayment(@ModelAttribute("userWrapper") UserWrapper userWrapper, Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Integer newAccount = user.getAccount() + userWrapper.getAccount();
        user.setAccount(newAccount);
        userService.save(user);
        return "redirect:/user";
    }

    @RequestMapping(value = "changeTariff", method = RequestMethod.GET)
    public String changeTariff(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        model.addAttribute("user", user);
        List<Tariff> tariffs = tariffService.findAll();
        model.addAttribute("tariffs", tariffs);
        model.addAttribute("tariff", new Tariff());

        model.addAttribute("emptyTariff", new Tariff());
        return "user/changeTariff";
    }

    @RequestMapping(value = "/changeTariff", params = {"changeTariff"}, method = RequestMethod.POST)
    public String changeTariff(@Valid @ModelAttribute("tariff") Tariff tariff, BindingResult br, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Long tar_id = tariff.getTariff_id();
        userService.addTariffToUser(user, tar_id);
        return "redirect:/user";
    }

    @RequestMapping(value = "/emptyTariff", params = {"changeTariff"}, method = RequestMethod.POST)
    public String emptyTariff(@Valid @ModelAttribute("emptyTariff") Tariff tariff, BindingResult br, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Long tar_id = tariff.getTariff_id();
        userService.addTariffToUser(user, tar_id);
        return "redirect:/user";
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public String services(Model model, Principal principal) {
        Long user_id = userService.findUserByUsername(principal.getName()).getUser_id();
        List<Service> servicesOfUser = userService.getServicesOfUser(user_id);
        model.addAttribute("servicesOfUser", servicesOfUser);
        return "user/services";
    }

    @RequestMapping(value = "/connectServices", method = RequestMethod.GET)
    public String connectServices(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        List<Service> servicesNonUser = userService.getServiceNonUser(user.getUser_id());
        model.addAttribute("servicesNonUser", servicesNonUser);
        model.addAttribute("userWrapper", new UserWrapper());
        return "user/connectServices";
    }

    @RequestMapping(value = "/connectServices", method = RequestMethod.POST)
    public String connectServices(@ModelAttribute("userWrapper") UserWrapper userWrapper, Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        List<Long> serviceIds = userWrapper.getServiceId();
        for (Long id : serviceIds) {
            userService.addServiceToUser(user.getUser_id(), id);
        }
        return "redirect:/user";
    }

    @RequestMapping(value = "/topUpAccount", method = RequestMethod.GET)
    public String topUpAccount(Model model) {
        model.addAttribute("userWrapper", new UserWrapper());
        return "user/topUpAccount";
    }

    @RequestMapping(value = "/topUpAccount", method = RequestMethod.POST)
    public String topUpAccount(@ModelAttribute("userWrapper") UserWrapper userWrapper, Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Integer newAccount = user.getAccount() + userWrapper.getAccount();
        user.setAccount(newAccount);
        userService.save(user);
        return "redirect:/user";
    }

}
