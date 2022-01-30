package com.example.application.views.createcount;

import com.example.application.data.entity.User;
import com.example.application.data.service.UserService;
import com.example.application.views.MainLayout;
import com.example.application.views.library.NewLibraryView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import elemental.json.Json;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A Designer generated component for the person-form-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but does not overwrite
 * or otherwise change this file.
 */
@PageTitle("Add User")
@Route(value = "new-add-user", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class NewAddUserView extends HorizontalLayout {

//    @Id("image")
    private Upload image;
//    @Id("imagePreview")
    private Image imagePreview;
//    @Id("name")
    private TextField name;
//    @Id("username")
    private TextField username;
//    @Id("hashedPassword")
    private PasswordField password;
//    @Id("confirmPassword")
    private PasswordField confirmPassword;
//    @Id("cancel")
    private Button cancel;
//    @Id
    private Button save;


    private User user;

    private UserService userService;


    public NewAddUserView(PasswordEncoder passwordEncoder, UserService userService) {
        this.userService = userService;

        VerticalLayout verticalLayout = new VerticalLayout();
        image = new Upload();
        imagePreview = new Image();
        attachImageUpload(image, imagePreview);
        name = new TextField();
        name.setLabel("Name and surname");
        name.setClearButtonVisible(true);
        name.setRequired(true);
        username = new TextField();
        username.setLabel("Login");
        username.setClearButtonVisible(true);
        username.setRequired(true);
        password = new PasswordField();
        password.setLabel("Password");
        password.setMinLength(8);
        confirmPassword = new PasswordField();
        confirmPassword.setLabel("Confirm password");
        confirmPassword.setMinLength(8);
        save = new Button("Save");
        cancel = new Button("Login");


        cancel.addClickListener(e -> UI.getCurrent().navigate("login"));
        save.addClickListener(e -> {
            if (!password.getValue().equals(confirmPassword.getValue())){
                confirmPassword.setErrorMessage("Passwords must matched");
            } else if(userService.userExists(username.getValue())) {
                username.setErrorMessage("Username is used");
                username.isInvalid();
            } else {
                user = new User();
                user.setName(name.getValue());
                user.setUsername(username.getValue());
                user.setHashedPassword(passwordEncoder.encode(password.getValue()));
                user.setProfilePictureUrl(imagePreview.getSrc());
                userService.save(user);
                Notification.show("SampleUser details stored.");
                UI.getCurrent().navigate(NewLibraryView.class);
            }
        });
        VerticalLayout layout = new VerticalLayout();
        verticalLayout.add(name, username, password, confirmPassword, save, cancel);
        layout.add(verticalLayout);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(layout);

    }

//    private void clearForm() {
//        populateForm(null);
//    }

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
        add(upload);
        preview.setVisible(false);
    }
//
    private void populateForm(User value) {
        this.user = value;
//        binder.readBean(this.user);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getProfilePictureUrl());
        }
    }

}
