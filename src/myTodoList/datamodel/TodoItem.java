package myTodoList.datamodel;

import java.time.LocalDate;

public class TodoItem {

    private String shortDescription;
    private String detail;
    private LocalDate deadline;

    public TodoItem(String shortDescription, String detail, LocalDate deadline) {
        this.shortDescription = shortDescription;
        this.detail = detail;
        this.deadline = deadline;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

/*
    @Override
    public String toString() {
        return this.shortDescription;
    }
*/
}
