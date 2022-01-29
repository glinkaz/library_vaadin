package com.example.application.views.library;

import com.example.application.data.entity.Book;
import com.example.application.data.service.BookService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriUtils;

@PageTitle("Library")
@Route(value = "hello/:sampleBookID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "old-library", layout = MainLayout.class)
@PermitAll
@Tag("library-view")
@JsModule("./views/library/library-view.ts")
public class LibraryView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    private final String SAMPLEBOOK_ID = "sampleBookID";
    private final String SAMPLEBOOK_EDIT_ROUTE_TEMPLATE = "hello/%d/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Grid<Book> grid;

    @Id
    private Upload image;
    @Id
    private Image imagePreview;
    @Id
    private TextField name;
    @Id
    private TextField author;
    @Id
    private DatePicker publicationDate;
    @Id
    private TextField pages;
    @Id
    private TextField isbn;

    @Id
    private Button cancel;
    @Id
    private Button save;
    @Id
    private TextField searchField;

    private BeanValidationBinder<Book> binder;

    private Book book;

    private BookService bookService;

    public LibraryView(@Autowired BookService bookService) {
        this.bookService = bookService;
        addClassNames("library-view", "flex", "flex-col", "h-full");
        TemplateRenderer<Book> imageRenderer = TemplateRenderer
                .<Book>of("<img style='height: 64px' src='[[item.image]]' />")
                .withProperty("image", Book::getImage);
        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn(Book::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Book::getAuthor).setHeader("Author").setAutoWidth(true);
        grid.addColumn(Book::getPublicationDate).setHeader("Publication Date").setAutoWidth(true);
        grid.addColumn(Book::getPages).setHeader("Pages").setAutoWidth(true);
        grid.addColumn(Book::getIsbn).setHeader("Isbn").setAutoWidth(true);
        grid.setItems(query -> bookService.list(null,
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEBOOK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(LibraryView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Book.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(pages).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("pages");

        binder.bindInstanceFields(this);

        attachImageUpload(image, imagePreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.book == null) {
                    this.book = new Book();
                }
                binder.writeBean(this.book);
                this.book.setImage(imagePreview.getSrc());

                bookService.update(this.book);
                clearForm();
                refreshGrid();
                Notification.show("SampleBook details stored.");
                UI.getCurrent().navigate(LibraryView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the sampleBook details.");
            }
        });
        List<Book> books = bookService.getBooks(null);
        GridListDataView<Book> dataView = grid.setItems(books);

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(book -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matchesTerm(book.getName(),
                    searchTerm);
            boolean matchesEmail = matchesTerm(book.getAuthor(), searchTerm);
            boolean matchesProfession = matchesTerm(book.getPages().toString(),
                    searchTerm);

            return matchesFullName || matchesEmail || matchesProfession;
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> sampleBookId = event.getRouteParameters().getInteger(SAMPLEBOOK_ID);
        if (sampleBookId.isPresent()) {
            Optional<Book> sampleBookFromBackend = bookService.get(sampleBookId.get());
            if (sampleBookFromBackend.isPresent()) {
                populateForm(sampleBookFromBackend.get());
            } else {
                Notification.show(String.format("The requested sampleBook was not found, ID = %d", sampleBookId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(LibraryView.class);
            }
        }
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            String mimeType = e.getMIMEType();
            String base64ImageData = Base64.getEncoder().encodeToString(uploadBuffer.toByteArray());
            String dataUrl = "data:" + mimeType + ";base64,"
                    + UriUtils.encodeQuery(base64ImageData, StandardCharsets.UTF_8);
            upload.getElement().setPropertyJson("files", Json.createArray());
            preview.setSrc(dataUrl);
            uploadBuffer.reset();
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Book value) {
        this.book = value;
        binder.readBean(this.book);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getImage());
        }

    }
    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
