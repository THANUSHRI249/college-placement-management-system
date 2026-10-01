package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import interfaces.EligibilityChecker;
import model.Application;
import model.Company;
import model.Job;
import model.Student;
import service.EligibilityResult;
import service.EligibilityService;
import service.JobService;
import service.PlacementService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Main Class - Entry point for the College Placement Management System.
 * Starts a built-in HTTP server on port 8080 serving the web UI and REST API.
 * Demonstrates Packages, Method calls, Object instantiation, and Java Standard APIs.
 */
public class PlacementManagementSystem {

    private static final int PORT = 8080;
    private static final PlacementService placementService = new PlacementService();
    private static final JobService jobService = new JobService();
    private static final EligibilityChecker eligibilityChecker = new EligibilityService();
    private static File webDirectory;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  COLLEGE PLACEMENT MANAGEMENT SYSTEM");
        System.out.println("  Done by THANUSHRI K, VALARMATHI R, SHALINI D");
        System.out.println("==================================================");

        // Locate web assets directory
        webDirectory = new File("web");
        if (!webDirectory.exists()) {
            webDirectory = new File("../web");
        }

        // Initialize default sample data
        seedData();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Static file handler for frontend UI (HTML, CSS, JS)
            server.createContext("/", new StaticFileHandler());

            // REST API Handlers
            server.createContext("/api/students", new StudentApiHandler());
            server.createContext("/api/companies", new CompanyApiHandler());
            server.createContext("/api/jobs", new JobApiHandler());
            server.createContext("/api/eligibility", new EligibilityApiHandler());
            server.createContext("/api/applications", new ApplicationApiHandler());
            server.createContext("/api/reports", new ReportApiHandler());

            server.setExecutor(null); // default executor
            server.start();

            System.out.println(" Server started successfully!");
            System.out.println(" Access the web interface at: http://localhost:" + PORT);
            System.out.println(" Press Ctrl+C in terminal to stop the server.");
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    /**
     * Seeds initial data into the system for demonstration.
     */
    private static void seedData() {
        // Sample Students
        Student s1 = new Student("THANUSHRI K", "710021104001", "CSE", 8.8, 86.5, 0, "Java, HTML, CSS, SQL, Python", "thanushri@example.com");
        Student s2 = new Student("VALARMATHI R", "710021104002", "CSE", 8.5, 82.0, 0, "Java, HTML, CSS, JavaScript", "valarmathi@example.com");
        Student s3 = new Student("SHALINI D", "710021104003", "ECE", 7.8, 75.0, 0, "C++, HTML, CSS, Embedded Systems", "shalini@example.com");
        Student s4 = new Student("RAGESH M", "710021104004", "IT", 6.8, 66.0, 1, "Java, HTML", "ragesh@example.com");

        placementService.addStudent(s1);
        placementService.addStudent(s2);
        placementService.addStudent(s3);
        placementService.addStudent(s4);

        // Sample Companies
        Company c1 = new Company("COMP101", "ABC Tech Solutions", "IT Services", "Software Developer");
        Company c2 = new Company("COMP102", "XYZ Innovations", "Product Development", "Systems Analyst");
        Company c3 = new Company("COMP103", "Global Data Corp", "Data Analytics", "Junior Data Engineer");

        jobService.addCompany(c1);
        jobService.addCompany(c2);
        jobService.addCompany(c3);

        // Sample Jobs with Eligibility Criteria
        Job j1 = new Job("JOB201", "Software Developer", "ABC Tech Solutions", "CSE", 7.5, 70.0, 0, "Java, HTML, CSS");
        Job j2 = new Job("JOB202", "Systems Analyst", "XYZ Innovations", "ANY", 7.0, 65.0, 0, "HTML, CSS");
        Job j3 = new Job("JOB203", "Junior Data Engineer", "Global Data Corp", "CSE", 8.0, 78.0, 0, "Java, SQL");

        jobService.addJob(j1);
        jobService.addJob(j2);
        jobService.addJob(j3);

        // Sample Applications & Placement Statuses
        placementService.applyForJob(s1, j1);
        placementService.updateApplicationStatus("APP101", "Selected");

        placementService.applyForJob(s2, j2);
        placementService.updateApplicationStatus("APP102", "Shortlisted");

        placementService.applyForJob(s3, j2);
        placementService.updateApplicationStatus("APP103", "Applied");
    }

    // ==========================================
    // HTTP HANDLERS & API IMPLEMENTATION
    // ==========================================

    /**
     * Handler to serve web UI files (index.html, style.css, app.js).
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(webDirectory, path);
            if (!file.exists() || file.isDirectory()) {
                file = new File(webDirectory, "index.html");
            }

            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript";
            }

            byte[] content = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, content.length);
            OutputStream os = exchange.getResponseBody();
            os.write(content);
            os.close();
        }
    }

    /**
     * REST API for Student Profile operations.
     */
    static class StudentApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                List<Student> students = placementService.getAllStudents();
                String json = studentsToJson(students);
                sendJsonResponse(exchange, 200, json);
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormOrJson(body);

                String name = params.getOrDefault("name", "");
                String regNo = params.getOrDefault("registerNumber", "");
                String dept = params.getOrDefault("department", "");
                double cgpa = parseDouble(params.get("cgpa"), 0.0);
                double percentage = parseDouble(params.get("percentage"), 0.0);
                int arrears = parseInt(params.get("arrears"), 0);
                String skills = params.getOrDefault("skills", "");
                String contact = params.getOrDefault("contactInfo", "");

                if (regNo.isEmpty() || name.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\": \"Name and Register Number are required.\"}");
                    return;
                }

                Student student = new Student(name, regNo, dept, cgpa, percentage, arrears, skills, contact);
                placementService.addStudent(student);
                sendJsonResponse(exchange, 200, "{\"message\": \"Student saved successfully\", \"registerNumber\": \"" + escapeJson(regNo) + "\"}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * REST API for Companies.
     */
    static class CompanyApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                List<Company> companies = jobService.getAllCompanies();
                String json = companiesToJson(companies);
                sendJsonResponse(exchange, 200, json);
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormOrJson(body);

                String name = params.getOrDefault("name", "");
                String id = params.getOrDefault("id", "COMP" + (jobService.getAllCompanies().size() + 101));
                String industry = params.getOrDefault("industry", "");
                String jobRole = params.getOrDefault("jobRole", "");

                if (name.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\": \"Company name is required.\"}");
                    return;
                }

                Company company = new Company(id, name, industry, jobRole);
                jobService.addCompany(company);
                sendJsonResponse(exchange, 200, "{\"message\": \"Company added successfully\"}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * REST API for Jobs.
     */
    static class JobApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                List<Job> jobs = jobService.getAllJobs();
                String json = jobsToJson(jobs);
                sendJsonResponse(exchange, 200, json);
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormOrJson(body);

                String title = params.getOrDefault("title", "");
                String companyName = params.getOrDefault("companyName", "");
                String id = params.getOrDefault("id", "JOB" + (jobService.getAllJobs().size() + 201));
                String requiredBranch = params.getOrDefault("requiredBranch", "ANY");
                double minCgpa = parseDouble(params.get("minCgpa"), 0.0);
                double minPercentage = parseDouble(params.get("minPercentage"), 0.0);
                int maxArrears = parseInt(params.get("maxArrears"), 0);
                String requiredSkills = params.getOrDefault("requiredSkills", "");

                if (title.isEmpty() || companyName.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\": \"Job Title and Company Name are required.\"}");
                    return;
                }

                Job job = new Job(id, title, companyName, requiredBranch, minCgpa, minPercentage, maxArrears, requiredSkills);
                jobService.addJob(job);
                sendJsonResponse(exchange, 200, "{\"message\": \"Job added successfully\"}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * REST API for Automated Student-Job Eligibility Matching.
     */
    static class EligibilityApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String regNo = queryParams.get("studentRegNo");

            if (regNo == null || regNo.isEmpty()) {
                sendJsonResponse(exchange, 400, "{\"error\": \"studentRegNo parameter is required.\"}");
                return;
            }

            Student student = placementService.getStudentByRegNo(regNo);
            if (student == null) {
                sendJsonResponse(exchange, 404, "{\"error\": \"Student with register number " + escapeJson(regNo) + " not found.\"}");
                return;
            }

            List<Job> allJobs = jobService.getAllJobs();
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < allJobs.size(); i++) {
                Job job = allJobs.get(i);
                EligibilityResult result = eligibilityChecker.evaluateEligibility(student, job);

                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"jobId\":\"").append(escapeJson(job.getId())).append("\",");
                json.append("\"jobTitle\":\"").append(escapeJson(job.getTitle())).append("\",");
                json.append("\"companyName\":\"").append(escapeJson(job.getCompanyName())).append("\",");
                json.append("\"requiredBranch\":\"").append(escapeJson(job.getRequiredBranch())).append("\",");
                json.append("\"minCgpa\":").append(job.getMinCgpa()).append(",");
                json.append("\"minPercentage\":").append(job.getMinPercentage()).append(",");
                json.append("\"maxArrears\":").append(job.getMaxArrears()).append(",");
                json.append("\"requiredSkills\":\"").append(escapeJson(String.join(", ", job.getRequiredSkills()))).append("\",");
                json.append("\"eligible\":").append(result.isEligible()).append(",");
                json.append("\"reasons\":[");
                for (int r = 0; r < result.getReasons().size(); r++) {
                    if (r > 0) json.append(",");
                    json.append("\"").append(escapeJson(result.getReasons().get(r))).append("\"");
                }
                json.append("]");
                json.append("}");
            }
            json.append("]");

            sendJsonResponse(exchange, 200, json.toString());
        }
    }

    /**
     * REST API for Application tracking.
     */
    static class ApplicationApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                List<Application> apps = placementService.getAllApplications();
                sendJsonResponse(exchange, 200, applicationsToJson(apps));
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormOrJson(body);

                String action = params.getOrDefault("action", "apply");

                if ("updateStatus".equalsIgnoreCase(action)) {
                    String appId = params.get("applicationId");
                    String status = params.get("status");
                    boolean updated = placementService.updateApplicationStatus(appId, status);
                    if (updated) {
                        sendJsonResponse(exchange, 200, "{\"message\": \"Status updated successfully\"}");
                    } else {
                        sendJsonResponse(exchange, 404, "{\"error\": \"Application not found\"}");
                    }
                } else {
                    // Apply for job
                    String regNo = params.get("studentRegNo");
                    String jobId = params.get("jobId");

                    Student student = placementService.getStudentByRegNo(regNo);
                    Job job = jobService.getJobById(jobId);

                    if (student == null || job == null) {
                        sendJsonResponse(exchange, 400, "{\"error\": \"Invalid Student or Job specified\"}");
                        return;
                    }

                    // Check eligibility before allowing application
                    EligibilityResult eval = eligibilityChecker.evaluateEligibility(student, job);
                    if (!eval.isEligible()) {
                        sendJsonResponse(exchange, 400, "{\"error\": \"Student is not eligible for this job requirement.\"}");
                        return;
                    }

                    Application app = placementService.applyForJob(student, job);
                    sendJsonResponse(exchange, 200, "{\"message\": \"Applied successfully\", \"applicationId\": \"" + escapeJson(app.getId()) + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * REST API for Placement Analytics & Report Generation.
     */
    static class ReportApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, Object> report = placementService.generateReport();
            @SuppressWarnings("unchecked")
            List<Application> apps = (List<Application>) report.get("applications");

            StringBuilder json = new StringBuilder("{");
            json.append("\"totalStudents\":").append(report.get("totalStudents")).append(",");
            json.append("\"totalApplications\":").append(report.get("totalApplications")).append(",");
            json.append("\"totalShortlisted\":").append(report.get("totalShortlisted")).append(",");
            json.append("\"totalSelected\":").append(report.get("totalSelected")).append(",");
            json.append("\"totalRejected\":").append(report.get("totalRejected")).append(",");
            json.append("\"applications\":").append(applicationsToJson(apps));
            json.append("}");

            sendJsonResponse(exchange, 200, json.toString());
        }
    }

    // ==========================================
    // UTILITY METHODS & JSON HELPERS
    // ==========================================

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(urlDecode(kv[0]), urlDecode(kv[1]));
            } else if (kv.length == 1) {
                map.put(urlDecode(kv[0]), "");
            }
        }
        return map;
    }

    private static Map<String, String> parseFormOrJson(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;

        String trimmed = body.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            // Simple JSON parser for single-level key-values
            String content = trimmed.substring(1, trimmed.length() - 1);
            String[] tokens = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String token : tokens) {
                String[] kv = token.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
                if (kv.length == 2) {
                    String k = cleanQuotes(kv[0].trim());
                    String v = cleanQuotes(kv[1].trim());
                    map.put(k, v);
                }
            }
        } else {
            // Form encoded
            String[] pairs = trimmed.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(urlDecode(kv[0]), urlDecode(kv[1]));
                }
            }
        }
        return map;
    }

    private static String cleanQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"") && str.length() >= 2) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private static String urlDecode(String val) {
        try {
            return URLDecoder.decode(val, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return val;
        }
    }

    private static double parseDouble(String val, double def) {
        try {
            return val != null ? Double.parseDouble(val.trim()) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static int parseInt(String val, int def) {
        try {
            return val != null ? Integer.parseInt(val.trim()) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    // JSON Serializers
    private static String studentsToJson(List<Student> students) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"name\":\"").append(escapeJson(s.getName())).append("\",");
            sb.append("\"registerNumber\":\"").append(escapeJson(s.getRegisterNumber())).append("\",");
            sb.append("\"department\":\"").append(escapeJson(s.getDepartment())).append("\",");
            sb.append("\"cgpa\":").append(s.getCgpa()).append(",");
            sb.append("\"percentage\":").append(s.getPercentage()).append(",");
            sb.append("\"arrears\":").append(s.getArrears()).append(",");
            sb.append("\"skills\":\"").append(escapeJson(String.join(", ", s.getSkills()))).append("\",");
            sb.append("\"contactInfo\":\"").append(escapeJson(s.getContactInfo())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String companiesToJson(List<Company> companies) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < companies.size(); i++) {
            Company c = companies.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":\"").append(escapeJson(c.getId())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(c.getName())).append("\",");
            sb.append("\"industry\":\"").append(escapeJson(c.getIndustry())).append("\",");
            sb.append("\"jobRole\":\"").append(escapeJson(c.getJobRole())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String jobsToJson(List<Job> jobs) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < jobs.size(); i++) {
            Job j = jobs.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":\"").append(escapeJson(j.getId())).append("\",");
            sb.append("\"title\":\"").append(escapeJson(j.getTitle())).append("\",");
            sb.append("\"companyName\":\"").append(escapeJson(j.getCompanyName())).append("\",");
            sb.append("\"requiredBranch\":\"").append(escapeJson(j.getRequiredBranch())).append("\",");
            sb.append("\"minCgpa\":").append(j.getMinCgpa()).append(",");
            sb.append("\"minPercentage\":").append(j.getMinPercentage()).append(",");
            sb.append("\"maxArrears\":").append(j.getMaxArrears()).append(",");
            sb.append("\"requiredSkills\":\"").append(escapeJson(String.join(", ", j.getRequiredSkills()))).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String applicationsToJson(List<Application> apps) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < apps.size(); i++) {
            Application a = apps.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":\"").append(escapeJson(a.getId())).append("\",");
            sb.append("\"studentRegNo\":\"").append(escapeJson(a.getStudentRegNo())).append("\",");
            sb.append("\"studentName\":\"").append(escapeJson(a.getStudentName())).append("\",");
            sb.append("\"companyName\":\"").append(escapeJson(a.getCompanyName())).append("\",");
            sb.append("\"jobTitle\":\"").append(escapeJson(a.getJobTitle())).append("\",");
            sb.append("\"status\":\"").append(escapeJson(a.getStatus())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }
}
