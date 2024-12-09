package com.example.coursework.controller;

import com.example.coursework.entity.Course;
import com.example.coursework.entity.Review;
import com.example.coursework.entity.User;
import com.example.coursework.UserContext;
import com.example.coursework.service.CourseService;
import com.example.coursework.service.ReviewService;
import com.example.coursework.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final CourseService courseService;

    public UserController(UserService userService, ReviewService reviewService, CourseService courseService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.courseService = courseService;
    }

    @GetMapping("/login")
    public String index(HttpServletRequest request) {
        String redirectUrl = request.getHeader("Referer");
        request.getSession().setAttribute("redirectUrl", redirectUrl);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) {

        Optional<User> user = userService.findUserByUsername(username);

        if (user.isPresent()) {
            if (BCrypt.checkpw(password, user.get().getPassword())) {
                UserContext.getInstance().setCurrentUser(user.get());

                String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
                if (redirectUrl != null) {
                    return "redirect:" + redirectUrl;
                }
            }
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationForm(HttpServletRequest request, Model model) {
        String redirectUrl = request.getHeader("Referer");
        request.getSession().setAttribute("redirectUrl", redirectUrl);

        if (!model.containsAttribute("username")) {
            model.addAttribute("username", "");
        }
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               HttpServletRequest request,
                               Model model) {

        String redirectUrl = request.getHeader("Referer");
        request.getSession().setAttribute("redirectUrl", redirectUrl);

        Optional<User> userByEmail = userService.findByEmail(email);
        Optional<User> userByUsername = userService.findByUsername(username);

        if (userByUsername.isPresent()) {
            model.addAttribute("username", "Имя уже занято");
            return "redirect:" + redirectUrl;
        }

        if (userByEmail.isPresent()) {
            model.addAttribute("email", "Email уже используется");
            return "redirect:" + redirectUrl;
        }

        userService.saveUser(new User(username, password, email, firstName, lastName));

        Optional<User> newUser = userService.findUserByUsername(username);

        if (newUser.isPresent()) {
            UserContext.getInstance().setCurrentUser(newUser.get());
        }

        if (redirectUrl != null) {
            return "redirect:" + redirectUrl;
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        String redirectUrl = request.getHeader("Referer");
        UserContext.getInstance().clearCurrentUser();

        if (redirectUrl != null && redirectUrl.contains("/profile")) {
            return "redirect:/";
        }
        if (redirectUrl != null && redirectUrl.contains("/admin")) {
            return "redirect:/";
        }

        if (redirectUrl != null) {
            return "redirect:" + redirectUrl;
        }

        return "redirect:/";
    }

    @GetMapping("/profile/{id}")
    public String getUserProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/edit_profile")
    public String editProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "redirect:/profile/" + id;
    }

    @PostMapping("/edit_profile/{id}")
    public String updateProfile(@PathVariable("id") Long id,
                                @RequestParam("username") String username,
                                @RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            model.addAttribute("user", userService.getUserById(id)); // добавляем пользователя в модель
            return "redirect:/profile/" + id;
        }

        User user = userService.getUserById(id);
        if (user != null) {
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            if (!password.isEmpty()) {
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            }

            userService.saveUser(user);
        }

        return "redirect:/profile/" + id;
    }

    @GetMapping("/teachers")
    public String showTeachersPage(Model model) {
        model.addAttribute("teachers", userService.getAllTeachers());
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) { // Проверяем, что пользователь существует
            model.addAttribute("user", user);}
        return "teachers";
    }
    @GetMapping("/aboutAuthor")
    public String showAboutAuthorPage(Model model) {
        model.addAttribute("aboutAuthor", userService.getAllTeachers());
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) { // Проверяем, что пользователь существует
            model.addAttribute("user", user);
        }
        return "aboutAuthor";
    }

    @PostMapping("/registerTeacher")
    public String registerTeacher(@RequestBody User user) {
        userService.registerTeacher(user);
        return "redirect:/teachers";
    }

    @GetMapping("/reviews")
    public String showReviewsPage(@RequestParam(required = false) Long courseId, Model model) {
        List<Review> reviews;

        if (courseId != null) {
            // Получить отзывы для конкретного курса
            reviews = reviewService.getReviewsByCourseId(courseId);
        } else {
            // Получить все отзывы
            reviews = reviewService.getAllReviews();
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("courseId", courseId); // Для сохранения состояния фильтра
        model.addAttribute("courses", courseService.getAllCourses()); // Список курсов
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "reviews";
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam String text, @RequestParam Long courseId) {
        User currentUser = UserContext.getInstance().getCurrentUser();

        if (currentUser != null) {
            Course course = courseService.getCourseById(courseId);
            if (course != null) {
                Review review = new Review();
                review.setUser(currentUser);
                review.setText(text);
                review.setCourse(course);
                review.setDate(LocalDateTime.now());
                reviewService.saveReview(review);
            }
        }
        return "redirect:/reviews";
    }

    @RequestMapping("/admin/deleteReview/{id}")
    public String deleteReview(@PathVariable(name = "id") Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String showAdminPanel(Model model) {
        User currentUser = UserContext.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            return "redirect:/admin";
        }

        List<User> users = userService.findAllUsers(); // Получаем список всех пользователей
        model.addAttribute("users", users);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("user", currentUser);
        model.addAttribute("reviews", reviewService.getAllReviews());


        return "admin"; // Возвращаем страницу админа
    }

    @PostMapping("/admin/changeRole")
    public String changeUserRole(@RequestParam Long userId, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findUserById(userId);

        if (user.isEmpty()) {
            // Добавляем сообщение об ошибке в атрибуты редиректа
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/admin"; // Редирект с ошибкой
        }

        user.get().setRole(newRole);
        userService.saveUser(user.get()); // Сохранение изменений в базе данных

        // Добавляем сообщение об успешном изменении роли
        redirectAttributes.addFlashAttribute("successMessage", "Роль успешно изменена");

        return "redirect:/admin"; // Редирект на страницу
    }

}
