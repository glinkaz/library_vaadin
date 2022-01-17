package com.example.application.views.library;

import com.example.application.data.entity.Book;
import com.example.application.data.service.BookService;
//import com.example.application.data.service.TagService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Select;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import elemental.json.Json;
import org.springframework.web.util.UriUtils;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * A Designer generated component for the person-form-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but does not overwrite
 * or otherwise change this file.
 */
@PageTitle("Add Book")
@Route(value = "add-book", layout = MainLayout.class)
@AnonymousAllowed
@Tag("add-book-view")
@JsModule("./views/library/add-book-view.ts")
@Uses(Icon.class)
public class AddBookView extends LitTemplate {

    @Id("image")
    private Upload image;
    @Id("imagePreview")
    private Image imagePreview;
    @Id("name")
    private TextField name;
    @Id("author")
    private TextField author;
    @Id("publicationDate")
    private DatePicker publicationDate;
    @Id("pages")
    private TextField pages;
    @Id("isbn")
    private TextField isbn;
    @Id("tags")
    private TextField tags;
    @Id("cancel")
    private Button cancel;
    @Id
    private Button save;


    private Book book;

    private BeanValidationBinder<Book> binder;

    private BookService bookService;

    public AddBookView(BookService bookService) {
        this.bookService = bookService;
//        this.tagService = tagService;

//        tags = new MultiSelectListBox<>();
//        tags.setItems(tagService.list());
//        tags.select(tagService.list().get(0));
//        tags.setRenderer(new ComponentRenderer<>(t ->
//                new Text(t.getName()))
//        );
//        tags.setItems(tagService.list().stream().map(t -> t.getName()).toList());
        binder = new BeanValidationBinder<>(Book.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(pages).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("pages");
        binder.bindInstanceFields(this);

        attachImageUpload(image, imagePreview);

//        cancel.addClickListener(e -> {
//            clearForm();
//        });

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            try {
//                if (this.book == null) {
//                    this.book = new Book();
//                }
                book = new Book();
                binder.writeBean(book);
                book.setImage(imagePreview.getSrc());

                bookService.update(book);
                clearForm();
                Notification.show("SampleBook details stored.");
                UI.getCurrent().navigate(NewLibraryView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception BOOK_TITLEhappened while trying to store the sampleBook details.");
            }
        });
    }

    private void clearForm() {
        populateForm(null);
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



}
