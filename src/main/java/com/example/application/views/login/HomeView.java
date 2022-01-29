package com.example.application.views.login;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route("login2")
public class HomeView extends VerticalLayout {

    RegisterItem registerItem;
    LoginItem loginItem;

    public HomeView() {

        addLoginItem();
        add(getContent());
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(loginItem, registerItem);
        content.setFlexGrow(1, loginItem);
        content.setFlexGrow(1, registerItem);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void addLoginItem(){
        loginItem = new LoginItem();
        registerItem = new RegisterItem();
    }

}
