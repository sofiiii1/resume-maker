package com.example.loginandregistration.controller;
import com.example.loginandregistration.dto.EducationDto;

import com.example.loginandregistration.dto.ExperienceDto;
import com.example.loginandregistration.dto.SkillDto;
import com.example.loginandregistration.model.*;
import com.example.loginandregistration.repository.ResumeRepository;
import com.example.loginandregistration.service.*;
import com.lowagie.text.DocumentException;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;
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
}
