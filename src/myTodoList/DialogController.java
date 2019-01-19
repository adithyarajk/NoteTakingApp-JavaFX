package myTodoList;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import myTodoList.datamodel.TodoData;
import myTodoList.datamodel.TodoItem;

import java.time.LocalDate;


public class DialogController {

    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailArea;
    @FXML
    private DatePicker deadlinePicker;

    public TodoItem processResults() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();
        TodoItem newItem =  new TodoItem(shortDescription, details, deadlineValue);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }

}
