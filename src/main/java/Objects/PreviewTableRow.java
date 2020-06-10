package Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public class PreviewTableRow {
    private IntegerProperty index = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty(this, "Name");
    private final StringProperty id = new SimpleStringProperty(this, "ID");
    private final StringProperty major = new SimpleStringProperty(this, "Major");
    private final StringProperty GPA = new SimpleStringProperty(this, "GPA");
    private final StringProperty project = new SimpleStringProperty(this, "Project");
    private final StringProperty gender = new SimpleStringProperty(this, "Gender");
    private final StringProperty identification = new SimpleStringProperty(this, "Identification");
    private final StringProperty placement = new SimpleStringProperty(this, "Placement");




    public PreviewTableRow(String name, String id, String major,/* String GPA,*/ String project, String gender/*, String identification, String placement*/) {
        this.name.set(name);
        this.id.set(id);
        this.major.set(major);
//        this.GPA.set(GPA);
        this.project.set(project);
        this.gender.set(gender);
//        this.identification.set(identification);
//        this.placement.set(placement);
    }

    public final Integer getIndex() {
        return index.get();
    }

    public final void setIndex(Integer value) {
        index.set(value);
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    //name
    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    //id
    public final StringProperty idProperty() {
        return this.id;
    }

    public final String getID() {
        return this.idProperty().get();
    }

    public final void setID(final String id) {
        this.idProperty().set(id);
    }

    //major
    public final StringProperty majorProperty() {
        return this.major;
    }

    public final String getMajor() {
        return this.majorProperty().get();
    }

    public final void setMajor(final String major) {
        this.majorProperty().set(major);
    }

    //project
    public final StringProperty projProperty() {
        return this.project;
    }

    public final String getProject() {
        return this.projProperty().get();
    }

    public final void setProject(final String project) {
        this.projProperty().set(project);
    }

    //gender
    public final StringProperty genderProperty() {
        return this.gender;
    }

    public final String getGender() {
        return this.genderProperty().get();
    }

    public final void setGender(final String gender) {
        this.genderProperty().set(gender);
    }

    //identification
    public final StringProperty identificationProperty() {
        return this.identification;
    }

    public final String getIdentification() { return this.identificationProperty().get(); }

    public final void setIdentification(final String identification) { this.identificationProperty().set(identification); }

    //placement
    public final StringProperty placementProperty() {
        return this.placement;
    }

    public final String getPlacement() { return this.placementProperty().get(); }

    public final void setPlacement(final String placement) { this.placementProperty().set(placement); }

    //GPA
    public final StringProperty gpaProperty() {
        return this.GPA;
    }

    public final String getGPA() { return this.gpaProperty().get(); }

    public final void setGPA(final String GPA) { this.gpaProperty().set(GPA); }


}