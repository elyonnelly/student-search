package api.parse;

public class Query {

    private String q;
    private String city;
    private Integer cityId;
    private String school;
    private Integer schoolId;
    private Integer age_from;
    private Integer age_to;
    private Integer grade;
    private StatusParticipant statusParticipant;

    public Query() {
        q = "";
        age_from = 13;
        age_to = 18;
        statusParticipant = StatusParticipant.PARTICIPANT;
        city = "";
        school = "";
    }

    public StatusParticipant getStatusParticipant() {
        return statusParticipant;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public void setStatusParticipant(StatusParticipant statusParticipant) {
        this.statusParticipant = statusParticipant;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAge_from(Integer age_from) {
        this.age_from = age_from;
    }

    public void setAge_to(Integer age_to) {
        this.age_to = age_to;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getAge_from() {
        return age_from;
    }

    public Integer getAge_to() {
        return age_to;
    }

    public Integer getCityId() {
        return cityId;
    }

    public String getCity() {
        return city;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    /**
     * @return имя пользователя, которого ищут
     */
    public String getQ() {
        return q;
    }

    @Override
    public String toString() {
        String s = q + " ";
        if (city != null && cityId != null) {
            s += city + " " + cityId.toString() + " ";
        }
        if (school != null && schoolId != null) {
            s += school + " " + schoolId.toString() + " ";
        }
        return s;
    }

    public Integer getGrade() {
        return grade;
    }
}
