package in.canaris.cloud.openstack.entity;

import java.util.List;

public class SubjectPlaylistView {
    private Integer subjectId;
    private String subjectName;
    private String subjectCode;
    private String departmentName;
    private String courseName;
    private String semesterName;
    private List<String> playlistNames;
    private List<String> subplaylistNames;
    private List<String> scenarioNames;
    
    // Getters and Setters
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }
    
    public List<String> getPlaylistNames() { return playlistNames; }
    public void setPlaylistNames(List<String> playlistNames) { this.playlistNames = playlistNames; }
    
    public List<String> getSubplaylistNames() { return subplaylistNames; }
    public void setSubplaylistNames(List<String> subplaylistNames) { this.subplaylistNames = subplaylistNames; }
    
    public List<String> getScenarioNames() { return scenarioNames; }
    public void setScenarioNames(List<String> scenarioNames) { this.scenarioNames = scenarioNames; }
}
