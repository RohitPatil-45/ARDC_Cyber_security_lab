package in.canaris.cloud.openstack.entity;


import java.util.List;

public class TeacherDTO {
    private String name;
    private List<String> subjects;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getSubjects() {
        return subjects;
    }
    
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}
