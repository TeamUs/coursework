package com.example.coursework.controller;

import com.example.coursework.entity.Course;
import com.example.coursework.entity.Review;
import com.example.coursework.service.CourseService;
import com.example.coursework.entity.User;
import com.example.coursework.UserContext;
import com.example.coursework.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AppController {

    @Autowired
    private CourseService service;
    @Autowired
    private ReviewService reviewService;
    @RequestMapping("/")
    public String viewHomePage(Model model, @Param("keyword") String keyword) {
        // Получаем все отзывы
        List<Review> allReviews = reviewService.getAllReviews();

        // Перемешиваем список отзывов и берем первые 5
        Collections.shuffle(allReviews);
        List<Review> randomReviews = allReviews.size() > 5 ? allReviews.subList(0, 5) : allReviews;

        model.addAttribute("reviews", randomReviews);

        // Получаем все курсы
        List<Course> allCourses = service.listAll(keyword);

        // Оставляем только первые 4 курса
        List<Course> courses = allCourses.size() > 4 ? allCourses.subList(0, 4) : allCourses;

        model.addAttribute("courses", courses);
        model.addAttribute("keyword", keyword);

        // Получаем текущего пользователя из UserContext и добавляем его в модель
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) { // Проверяем, что пользователь существует
            model.addAttribute("user", user);
        }
        return "index";
    }


    // Метод для отображения подробностей курса
    @GetMapping("/courses/{id}")
    public String showCourseDetails(@PathVariable("id") Long id, Model model) {
        Course course = service.getCourseById(id);
        model.addAttribute("course", course);
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) { // Проверяем, что пользователь существует
            model.addAttribute("user", user);
        }
        return "course_details";  // Имя шаблона для отображения подробностей
    }

    @RequestMapping("/new")
    public String showNewCourseForm(Model model) {
        Course course = new Course();
        model.addAttribute("courses", course);
        return "new_course";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Object> saveCourse(@RequestBody Course course) {
        service.save(course);
        return ResponseEntity.ok().build(); // Сообщаем о успешном выполнении
    }


    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {

        Course course = service.get(id);
        if (course == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(course); // 200 OK с данными книги
    }

    @RequestMapping("/delete/{id}")
    public String deleteCourse(@PathVariable(name = "id") Long id) {
        reviewService.deleteByCourseId(id); // Удаляем связанные отзывы
        service.delete(id); // Удаляем курс
        return "redirect:/courses";
    }
    @GetMapping("/startDateTime")
    @ResponseBody
    public Map<String, Integer> getCourseDate() {
        List<Course> courses = service.listAll(null); // Получаем все книги
        Map<String, Integer> CourseDate = new HashMap<>();

        for (Course course : courses) {
            if (course.getStartDateTime() != null) {
                String date = course.getStartDateTime().toString(); // Форматируем дату
                CourseDate.put(date, CourseDate.getOrDefault(date, 0) + 1); // Увеличиваем счетчик
            }
        }
        return CourseDate; // Возвращаем карту с данными
    }
    @GetMapping("/schedule")
    public String showSchedule(
            Model model,
            @RequestParam(value = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(value = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime
    ) {
        List<Course> listCourses;
        boolean noCoursesFound = false;

        // Если параметры не переданы, показываем все курсы
        if (startDateTime == null || endDateTime == null) {
            listCourses = service.getAllCourses(); // Получаем все курсы
        } else {
            // Если параметры переданы, фильтруем курсы по диапазону дат
            listCourses = service.findCoursesByDateRange(startDateTime, endDateTime);

            // Если курсов не найдено, устанавливаем флаг
            if (listCourses.isEmpty()) {
                noCoursesFound = true;
            }
        }

        model.addAttribute("listCourses", listCourses);
        model.addAttribute("noCoursesFound", noCoursesFound);  // Флаг для проверки наличия курсов

        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "schedule"; // Возвращаем представление расписания
    }


    @RequestMapping("/courses")
    public String courses(Model model, @Param("keyword") String keyword) {
        List<Course> listCourses = service.listAll(keyword);
        model.addAttribute("listCourses", listCourses);
        model.addAttribute("keyword", keyword);

        // Проверка на наличие курсов
        boolean noCoursesFound = listCourses.isEmpty();
        model.addAttribute("noCoursesFound", noCoursesFound);

        // Получаем текущего пользователя из UserContext и добавляем его в модель
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "courses";
    }


}