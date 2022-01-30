package com.example.application.views.createcount;

import com.example.application.data.entity.Book;
import com.example.application.data.entity.User;
import com.example.application.data.service.BookService;
import com.example.application.data.service.UserService;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.MainLayout;
import com.example.application.views.library.NewLibraryView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import elemental.json.Json;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriUtils;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * A Designer generated component for the person-form-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but does not overwrite
 * or otherwise change this file.
 */
@PageTitle("Add User")
@Route(value = "add-user", layout = MainLayout.class)
@AnonymousAllowed
@Tag("add-user-view")
@JsModule("./views/library/add-user-view.ts")
@Uses(Icon.class)
public class AddUserView extends LitTemplate {

    @Id("image")
    private Upload image;
    @Id("imagePreview")
    private Image imagePreview;
    @Id("name")
    private TextField name;
    @Id("username")
    private TextField username;
    @Id("hashedPassword")
    private PasswordField password;
    @Id("confirmPassword")
    private PasswordField confirmPassword;
    @Id("cancel")
    private Button cancel;
    @Id
    private Button save;


    private User user;

    private BeanValidationBinder<User> binder;

    private UserService userService;


    public AddUserView(PasswordEncoder passwordEncoder, UserService userService) {
        this.userService = userService;

        binder = new BeanValidationBinder<>(User.class);

        // Bind fields. This where you'd define e.g. validation rules
//        binder.forField(pages).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("pages");
        binder.bindInstanceFields(this);

        attachImageUpload(image, imagePreview);

//        cancel.addClickListener(e -> {
//            clearForm();
//        });

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            try {
                if (password.getValue().equals(confirmPassword.getValue())){
                user = new User();
                binder.writeBean(user);
                user.setProfilePictureUrl(imagePreview.getSrc());
                user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
                userService.update(user);
                clearForm();
                Notification.show("SampleUser details stored.");
                UI.getCurrent().navigate(NewLibraryView.class);
                } else {
                    confirmPassword.setErrorMessage("Passwords must matched");
                }
            } catch (ValidationException validationException) {
                Notification.show("An exception BOOK_TITLEhappened while trying to store the sampleUser details.");
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

    private void populateForm(User value) {
        this.user = value;
        binder.readBean(this.user);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getProfilePictureUrl());
        }
    }

}
