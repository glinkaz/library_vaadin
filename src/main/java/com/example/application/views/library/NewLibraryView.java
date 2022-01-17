package com.example.application.views.library;

import com.example.application.data.entity.Book;
import com.example.application.data.service.BookService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Objects;

@Slf4j
@PageTitle("NewLibrary")
@Route(value = "library/:bookID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Tag("new-library-view")
@JsModule("./views/library/new-library-view.ts")
public class NewLibraryView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    @Id("grid")
    private Grid<Book> grid;
    @Id
    private TextField searchField;
    @Id("borrowed-combobox")
    private ComboBox<String> borrowedComboBox;

    private BookService bookService;

    public NewLibraryView(@Autowired BookService bookService) {
        this.bookService = bookService;
        Editor<Book> editor = grid.getEditor();


        addClassNames("library-view", "flex", "flex-col", "h-full");
        TemplateRenderer<Book> imageRenderer = TemplateRenderer
                .<Book>of("<img style='height: 64px' src='[[item.image]]' />")
                .withProperty("image", Book::getImage);

        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn(Book::getName, "name").setHeader("Name").setAutoWidth(true);
        grid.addColumn(Book::getAuthor, "author").setHeader("Author").setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getPublicationDate, "publicationDate").setHeader("Publication Date").setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getPages, "pages").setHeader("Pages").setComparator((p1, p2) -> p1.getPages() - p2.getPages()).setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getIsbn, "isbn").setHeader("Isbn").setAutoWidth(true).setSortable(true);
        Grid.Column<Book> borrowedColumn = grid.addColumn(Book::getBorrowed, "borrowed").setHeader("Borrowed").setAutoWidth(true).setSortable(true);
        Grid.Column<Book> tagsColumn = grid.addColumn(Book::getTags, "tags").setHeader("Tags").setAutoWidth(true).setSortable(true);
        Grid.Column<Book> editColumn = grid.addComponentColumn(person -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                log.warn("Clicked edit button - " + person.getName());
                if (editor.isOpen())
                    log.info("Open another editor");
                    editor.cancel();
                grid.getEditor().editItem(person);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, bookButton) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.removeBook(bookButton));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Manage");



        grid.setItems(query -> bookService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.asSingleSelect().addValueChangeListener(event -> {
            //todo when selected item
            Notification notification = Notification.show("Clicked item!");
        });

        List<Book> books = bookService.getBooks();
        GridListDataView<Book> dataView = grid.setItems(books);

        borrowedComboBox.setAllowCustomValue(true);
        borrowedComboBox.setItems("All", "Borrowed", "Not Borrowed");
        grid.setItems(books).addFilter(book -> {

            if (Objects.equals(borrowedComboBox.getValue(), "Not Borrowed")){
                return book.getBorrowed() == null || book.getBorrowed().isEmpty() || book.getBorrowed().isBlank();
            }
            else if(Objects.equals(borrowedComboBox.getValue(), "Borrowed")){
                return book.getBorrowed() != null && !book.getBorrowed().isEmpty() && !book.getBorrowed().isBlank();
            }else{
                return true;
            }

        });
        borrowedComboBox.addValueChangeListener(e -> grid.getListDataView().refreshAll());

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(book -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = matchesTerm(book.getName(), searchTerm);
            boolean matchesAuthor = matchesTerm(book.getAuthor(), searchTerm);
            boolean matchesIsbn = matchesTerm(book.getIsbn(), searchTerm);
            boolean matchesPublicationDate = matchesTerm(book.getPublicationDate().toString(), searchTerm);
            boolean matchesPages = matchesTerm(book.getPages().toString(), searchTerm);
            boolean matchesBorrowed = matchesTerm(book.getBorrowed(), searchTerm);
            boolean matchesTags = matchesTerm(book.getTags(), searchTerm);

            return matchesName || matchesAuthor || matchesIsbn || matchesPublicationDate || matchesPages
                    || matchesBorrowed || matchesTags;

        });

        Binder<Book> binder = new Binder<>(Book.class);

        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField borrowedField = new TextField();
        borrowedField.setWidthFull();
        binder.forField(borrowedField)
                .bind(Book::getBorrowed, Book::setBorrowed);
        borrowedField.getValue();
        //Book -> wstawić do booka warośc borrower -> updateuj booka z nową wartością borrower
        borrowedColumn.setEditorComponent(borrowedField);

        TextField tagsField = new TextField();
        tagsField.setWidthFull();
        binder.forField(tagsField)
                .bind(Book::getTags, Book::setTags);
        tagsColumn.setEditorComponent(tagsField);

        Button saveButton = new Button("Save", e -> {
            log.info("Zuzia - editor - " + editor.getItem().getBorrowed());
            Book saveBook = editor.getItem();
            saveBook.setBorrowed(borrowedField.getValue());
            log.info("Zuzia - Save button - " + borrowedField.getValue());
            bookService.update(saveBook);
            log.info("Zuzia - New Book object - " + saveBook.toString());
            editor.save();
            refreshGrid();
//            log.info("editor - " + editor.getItem().getBorrowed());
        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> {
            editor.cancel();
            log.info("Zuzia - Canel button ");
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

    }



    private boolean matchesTerm(String value, String searchTerm) {
        String v = value == null ? "" : value;
        return v.toLowerCase().contains(searchTerm.toLowerCase());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }


    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void removeBook(Book book) {
        if (book == null)
            return;
        bookService.delete(book.getId());
        this.refreshGrid();
    }


}


