package myTodoList;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import myTodoList.datamodel.TodoData;
import myTodoList.datamodel.TodoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<TodoItem> todoItems;

    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<TodoItem> filteredList;

    private Predicate<TodoItem> wantAllItems;

    private Predicate<TodoItem> wantTodaysItems;

    public void initialize() {

        /*TodoItem item1 = new TodoItem("Mail Birthday card", "Buy a 30th birhtay card for john",
                LocalDate.of(2018, Month.OCTOBER,28));
        TodoItem item2 = new TodoItem("Doctors Appointment", "Check up for head releated diseases",
                LocalDate.of(2018, Month.NOVEMBER,8));
        TodoItem item3 = new TodoItem("Study for Test", "Class test that take up 25 marks",
                LocalDate.of(2018, Month.DECEMBER,19));
        TodoItem item4 = new TodoItem("Buy groceries ", "Stock up on supplies for apocalypse",
                LocalDate.of(2018, Month.OCTOBER,2));
        TodoItem item5 = new TodoItem("Holiday plans", "Vacation to maldives book flight tickets",
                LocalDate.of(2018, Month.DECEMBER,16));

        todoItems = new ArrayList<>();
        todoItems.add(item1);
        todoItems.add(item2);
        todoItems.add(item3);
        todoItems.add(item4);
        todoItems.add(item5);

        TodoData.getInstance().setTodoItems(todoItems);*/


        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);

        todoListView.getSelectionModel().selectedItemProperty().addListener((observableValue, todoItem, t1) -> {
            if(t1 != null) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                itemDetailsTextArea.setText(item.getDetail());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, YYYY");
                deadlineLabel.setText(df.format(item.getDeadline()));
            }
        });

        wantAllItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem item) {
                return true;
            }
        };

        wantTodaysItems = item -> item.getDeadline().equals(LocalDate.now());

        filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(),
                new Predicate<TodoItem>() {
                    @Override
                    public boolean test(TodoItem item) {
                        return false;
                    }
                });

        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList,
                new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                }

        );

/*
        todoListView.setItems(TodoData.getInstance().getTodoItems());
*/
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean b) {
                        super.updateItem(todoItem, b);
                        if(b) {
                            setText(null);
                        } else {
                            setText(todoItem.getShortDescription());
                            if(todoItem.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (todoItem.getDeadline().equals(LocalDate.now().plusDays(1))) {

                            }
                        }

                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNotEmpty) -> {
                            if(isNotEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });


                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new Todo Item");
        dialog.setHeaderText("Use this dialog to create new todo Item");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.printf("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            TodoItem newItem = controller.processResults();
            todoListView.getSelectionModel().select(newItem);
            System.out.println("OK Pressed");
        } else {
            System.out.println("Cancel Pressed");
        }

    }


    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }

    }

    @FXML
    public void handleClickListView() {
        TodoItem item =  todoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetail());
        deadlineLabel.setText(item.getDeadline().toString());

        /*System.out.println(item);
        StringBuilder sb = new StringBuilder(item.getDetail());
        sb.append("\n\n\n");
        sb.append("Due :");
        sb.append(item.getDeadline().toString());
        itemDetailsTextArea.setText(sb.toString());
*/

    }

    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete todo item");
        alert.setHeaderText(item.getShortDescription());
        alert.setContentText("Are you sure ? press OK to confirm or cancel to go back ");
        Optional<ButtonType>  result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteTodoItem(item);

        }
    }

    @FXML
    public void handleFilterButton() {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if(filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadlineLabel.setText("");
            } else if(filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }

        } else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }


}
