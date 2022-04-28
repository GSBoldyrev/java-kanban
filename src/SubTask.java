public class SubTask extends Task {
  private int epicId;

  public SubTask(String name, String description, String status, int epicId) {
    super(name, description, status);
    this.epicId = epicId;
  }

  @Override
  public String toString() {
    return "SubTask{" +
            "memberOfEpic=" + epicId +
            ", name='" + super.getName() + '\'' +
            ", description='" + super.getDescription() + '\'' +
            ", status='" + super.getStatus() + '\'' +
            '}';
  }

  public int getEpicId() {
    return epicId;
  }

  public void setEpicId(int epicId) {
    this.epicId = epicId;
  }
}
