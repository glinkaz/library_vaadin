package com.example.application.views.library;

import com.example.application.data.entity.Book;
import com.example.application.data.service.BookService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.PermitAll;

@PageTitle("NewLibrary")
@Route(value = "library/:bookID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "library", layout = MainLayout.class)
@PermitAll
@Tag("new-library-view")
@JsModule("./views/library/new-library-view.ts")
public class NewLibraryView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    @Id
    private Grid<Book> grid;

    private Book book;

    private BookService bookService;

    public NewLibraryView(BookService bookService) {
        this.bookService = bookService;
        addClassNames("library-view", "flex", "flex-col", "h-full");
        TemplateRenderer<Book> imageRenderer = TemplateRenderer
                .<Book>of("<img style='height: 64px' src='[[item.image]]' />")
                .withProperty("image", Book::getImage);
        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn(Book::getName).setHeader("Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getAuthor).setHeader("Author").setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getPublicationDate).setHeader("Publication Date").setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getPages).setHeader("Pages").setAutoWidth(true).setSortable(true);
        grid.addColumn(Book::getIsbn).setHeader("Isbn").setAutoWidth(true).setSortable(true);
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
