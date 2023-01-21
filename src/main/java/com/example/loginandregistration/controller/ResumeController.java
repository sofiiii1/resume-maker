package com.example.loginandregistration.controller;
import com.example.loginandregistration.dto.EducationDto;

import com.example.loginandregistration.dto.ExperienceDto;
import com.example.loginandregistration.dto.SkillDto;
import com.example.loginandregistration.model.*;
import com.example.loginandregistration.repository.ExperienceRepository;
import com.example.loginandregistration.repository.ResumeRepository;
import com.example.loginandregistration.repository.UserRepository;
import com.example.loginandregistration.service.*;
import com.lowagie.text.DocumentException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.FileSystems;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


@Controller
public class ResumeController {

    private ResumeService resumeService;
    private UserService userService;
    private EducationService educationService;
    private ExperienceService experienceService;
    private SkillService skillService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    public ResumeController(ResumeService resumeService, UserService userService, EducationService educationService, ExperienceService experienceService, SkillService skillService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.educationService=educationService;
        this.experienceService=experienceService;
        this.skillService=skillService;
    }

    @GetMapping("/add")
    public String newResume(Model model){
        model.addAttribute("resume", new Resume());
        model.addAttribute("today", LocalDate.now());
        return "resume_form";
    }

    @PostMapping("/save-resume")
    public String saveResume(@ModelAttribute("resume") @Valid Resume resume, BindingResult result, Principal principal){
        if (result.hasErrors()) {
            return "resume_form";
        }
        User user=userService.findByEmail(principal.getName());
        resume.setUser(user);
        resumeService.save(resume);
        return "redirect:/addEducation/"+resume.getId();
    }

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model){
        User user=userService.findByEmail(principal.getName());
        model.addAttribute("name",user.getFirstName()+" "+ user.getLastName());
        model.addAttribute("email",user.getEmail());
        return "profile";
    }
    @GetMapping("/addEducation/{id}")
    public String addEducation(@PathVariable("id")Integer id, Model model){
        Resume resume=resumeService.getById(id);
        model.addAttribute("resume", resume);
        model.addAttribute("today", LocalDate.now());
        return "education_form";
    }

    @PostMapping(value="/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getData(@RequestBody EducationDto educationDto){
        Education education=new Education(educationDto.getInstitution(), educationDto.getFaculty(),
                educationDto.getDateFrom(), educationDto.getDateTo(), educationDto.getDescription());
        education.setResume(resumeService.getById(educationDto.getResumeId()));
        educationService.save(education);
        return "redirect:/add";
    }

    @GetMapping("/addExperience/{id}")
    public String addExperience(@PathVariable("id")Integer id, Model model){
        Resume resume=resumeService.getById(id);
        model.addAttribute("resume",resume);
        model.addAttribute("today", LocalDate.now());
        return "experience_form";
    }

    @PostMapping(value="/getDataExperience", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDataExperience(@RequestBody ExperienceDto experienceDto){
        Experience experience=new Experience(experienceDto.getCompany(), experienceDto.getPosition(),
                experienceDto.getDateFrom(), experienceDto.getDateTo(), experienceDto.getDescription());
        experience.setResume(resumeService.getById(experienceDto.getResumeId()));
        experienceService.save(experience);
        return "redirect:/add";
    }

    @GetMapping("/addSkill/{id}")
    public String addSkill(@PathVariable("id") Integer id, Model model) {
        Resume resume=resumeService.getById(id);
        model.addAttribute("resume", resume);
        return "skill_form";
    }
    @PostMapping(value="/getDataSkill", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDataExperience(@RequestBody SkillDto skillDto){
        Skill skill=new Skill(skillDto.getSkillName(), Rating.valueOfLabel(skillDto.getSkillRating()));
        skill.setResume(resumeService.getById(skillDto.getResumeId()));
        skillService.save(skill);
        return "redirect:/add";
    }

    @GetMapping("/generated_resume/{id}")
    public String generatedResume(Model model, @PathVariable("id") Integer id){
        Resume currentResume=resumeService.getById(id);
        List<Education> educationList=currentResume.getEducationList();
        List<Experience> experienceList=currentResume.getExperienceList();
        List<Skill> skillList=currentResume.getSkillList();

        model.addAttribute("resume", currentResume);
        model.addAttribute("educationList", educationList);
        model.addAttribute("experienceList", experienceList);
        model.addAttribute("skillList", skillList);

        return "generated_resume";
    }

    @GetMapping("/allResumes")
    public  String getAllResumes(Model model, Principal principal){
        User current=userService.findByEmail(principal.getName());
        List<Resume> resumes=current.getResumes();
        model.addAttribute("resumes", resumes);
        return "all_resumes";
    }

    @RequestMapping (value="/download_resume/{id}")
    public ResponseEntity<byte[]> downloadPDFResource(@PathVariable("id")Integer id) throws IOException, DocumentException{
        Context context = new Context();
        Resume resume=resumeService.getById(id);
        context.setVariable("resume",resume);
        context.setVariable("educationList",resume.getEducationList());
        context.setVariable("experienceList",resume.getExperienceList());
        context.setVariable("skillList",resume.getSkillList());

        String htmlContentToRender = templateEngine.process("download_resume", context);
        ByteArrayInputStream byteArrayInputStream=null;
        try{
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            String xHtml = xhtmlConvert(htmlContentToRender);
            String baseUrl = FileSystems
                    .getDefault()
                    .getPath("src", "main", "resources","templates")
                    .toUri()
                    .toURL()
                    .toString();
            renderer.setDocumentFromString(xHtml, baseUrl);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream, false);
            renderer.finishPDF();
            byteArrayInputStream=new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }catch(DocumentException e){
            e.getCause();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume"+resume.getId()+".pdf");
        ResponseEntity<byte[]> response=new ResponseEntity<>(byteArrayInputStream.readAllBytes(),headers, HttpStatus.OK);
        return response;
    }
    private String xhtmlConvert(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString("UTF-8");
    }

    @GetMapping("/editResume/{id}")
    public String editResume(@PathVariable("id")Integer id, Model model){
        Resume resume=resumeService.getById(id);
        model.addAttribute("resume", resume);
        return "resume_edit_form";
    }

    @PutMapping("/editResume/{id}")
    public String updateResume(@PathVariable("id") Integer id, @RequestParam  String firstName, @RequestParam String lastName, @RequestParam  String dateOfBirth, @RequestParam String phone, @RequestParam String email, @RequestParam String biography) throws Exception{
        Resume updateResume=resumeService.getById(id);
        updateResume.setFirstName(firstName);
        updateResume.setLastName(lastName);
        updateResume.setDateOfBirth(new SimpleDateFormat("yyyy-mm-dd").parse(dateOfBirth));
        updateResume.setPhone(phone);
        updateResume.setEmail(email);
        updateResume.setBiography(biography);
        resumeService.save(updateResume);
        return "redirect:/generated_resume/"+id;
    }

    @GetMapping("/deleteResume/{id}")
    public String deleteResume(@PathVariable("id")Integer id){
        resumeService.deleteById(id);
        return "redirect:/allResumes";
    }

    @GetMapping("/editSkill/{id}")
    public String editSkill(@PathVariable("id") Integer id, Model model){
        Skill skill=skillService.getById(id);
        model.addAttribute("skill", skill);
        return "skill_edit_form";
    }
    @PutMapping("/editSkill/{id}")
    public String updateSkill(@PathVariable("id") Integer id, @RequestParam String skillName, @RequestParam String skillRating){
        Skill updateSkill=skillService.getById(id);
        updateSkill.setSkillName(skillName);
        updateSkill.setSkillRating(Rating.valueOfLabel(skillRating));
        skillService.save(updateSkill);
        return "redirect:/generated_resume/"+updateSkill.getResume().getId();

    }
    @GetMapping("/deleteSkill/{id}")
    public String deleteSkill(@PathVariable("id") Integer id){
        Skill skill=skillService.getById(id);
        Integer resumeId=skill.getResume().getId();
        skillService.deleteById(id);
        return "redirect:/generated_resume/"+resumeId;
    }
    @GetMapping("/editEducation/{id}")
    public String editEducation(@PathVariable("id") Integer id, Model model){
        Education education=educationService.getById(id);
        model.addAttribute("education", education);
        return "education_edit_form";
    }

    @PutMapping("/editEducation/{id}")
    public String updateEducation(@PathVariable("id") Integer id, @RequestParam String institution, @RequestParam String faculty, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String description) throws Exception{
        Education updatedEducation=educationService.getById(id);
        updatedEducation.setInstitution(institution);
        updatedEducation.setFaculty(faculty);
        updatedEducation.setDateFrom(new SimpleDateFormat("yyyy-mm-dd").parse(dateFrom));
        updatedEducation.setDateTo(new SimpleDateFormat("yyyy-mm-dd").parse(dateTo));
        updatedEducation.setDescription(description);
        educationService.save(updatedEducation);
        return "redirect:/generated_resume/"+updatedEducation.getResume().getId();
    }
    @GetMapping("/deleteEducation/{id}")
    public String deleteEducation(@PathVariable("id") Integer id){
        Education education=educationService.getById(id);
        Integer resumeId=education.getResume().getId();
        educationService.deleteById(id);
        return "redirect:/generated_resume/"+resumeId;
    }

    @GetMapping("/editExperience/{id}")
    public String editExperience(@PathVariable("id") Integer id, Model model){
        Experience experience=experienceService.getById(id);
        model.addAttribute("experience", experience);
        return "experience_edit_form";
    }
    @GetMapping("/deleteExperience/{id}")
    public String deleteExperience(@PathVariable("id") Integer id){
        Experience experience=experienceService.getById(id);
        Integer resumeId=experience.getResume().getId();
        experienceService.deleteById(id);
        return "redirect:/generated_resume/"+resumeId;
    }
    @PutMapping("/editExperience/{id}")
    public String updateExperience(@PathVariable("id") Integer id, @RequestParam String company, @RequestParam String position, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String description) throws Exception{
        Experience experience=experienceService.getById(id);
        experience.setCompany(company);
        experience.setPosition(position);
        experience.setDateFrom(new SimpleDateFormat("yyyy-mm-dd").parse(dateFrom));
        experience.setDateTo(new SimpleDateFormat("yyyy-mm-dd").parse(dateTo));
        experience.setDescription(description);
        experienceService.save(experience);
        return "redirect:/generated_resume/"+experience.getResume().getId();
    }



}
