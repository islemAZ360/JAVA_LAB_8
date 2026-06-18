package client.gui.components.pagination;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import client.gui.components.button.ButtonSize;
import client.gui.components.button.ButtonVariant;
import client.gui.components.button.UiButton;
import java.util.function.Consumer;

public class UiPagination extends HBox {
    private int currentPage;
    private int totalPages;
    private Consumer<Integer> onPageChange;
    private final Label pageInfo = new Label();

    public UiPagination(int currentPage, int totalPages) {
        getStyleClass().add("ui-pagination");
        setAlignment(Pos.CENTER);
        setSpacing(4);
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        rebuild();
    }

    public void setOnPageChange(Consumer<Integer> cb) {
        this.onPageChange = cb;
    }

    public void setCurrentPage(int page) {
        this.currentPage = Math.max(1, Math.min(page, totalPages));
        rebuild();
    }

    public void setTotalPages(int total) {
        this.totalPages = Math.max(1, total);
        this.currentPage = Math.min(currentPage, totalPages);
        rebuild();
    }

    public int getCurrentPage() { return currentPage; }

    private void rebuild() {
        getChildren().clear();

        UiButton prev = new UiButton("‹ Prev", ButtonVariant.OUTLINE);
        prev.setDisable(currentPage <= 1);
        prev.setOnAction(e -> goTo(currentPage - 1));

        UiButton next = new UiButton("Next ›", ButtonVariant.OUTLINE);
        next.setDisable(currentPage >= totalPages);
        next.setOnAction(e -> goTo(currentPage + 1));

        pageInfo.setText("Page " + currentPage + " of " + totalPages);
        pageInfo.getStyleClass().add("ui-pagination-info");

        HBox numbers = new HBox(2);
        numbers.setAlignment(Pos.CENTER);
        int start = Math.max(1, currentPage - 2);
        int end   = Math.min(totalPages, start + 4);
        start = Math.max(1, end - 4);

        if (start > 1) {
            numbers.getChildren().add(pageNumBtn(1));
            if (start > 2) numbers.getChildren().add(ellipsis());
        }
        for (int i = start; i <= end; i++) {
            UiButton nb = pageNumBtn(i);
            if (i == currentPage) nb.applyVariant(ButtonVariant.DEFAULT);
            numbers.getChildren().add(nb);
        }
        if (end < totalPages) {
            if (end < totalPages - 1) numbers.getChildren().add(ellipsis());
            numbers.getChildren().add(pageNumBtn(totalPages));
        }

        getChildren().addAll(prev, numbers, next);
    }

    private UiButton pageNumBtn(int page) {
        UiButton btn = new UiButton(String.valueOf(page), ButtonVariant.GHOST);
        btn.getStyleClass().add("ui-pagination-num");
        btn.setOnAction(e -> goTo(page));
        return btn;
    }

    private Label ellipsis() {
        Label l = new Label("…");
        l.getStyleClass().add("ui-pagination-ellipsis");
        return l;
    }

    private void goTo(int page) {
        currentPage = Math.max(1, Math.min(page, totalPages));
        rebuild();
        if (onPageChange != null) onPageChange.accept(currentPage);
    }
}
