package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();

        if (card.getContainingPile().getPileType() == Pile.PileType.STOCK) {
            card.moveToPile(discardPile);
            card.flip();
            card.setMouseTransparent(false);
            System.out.println("Placed " + card + " to the waste.");

        }
        if(card.getContainingPile().getTopCard().isFaceDown() ){
            card.flip();
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        if (!card.isFaceDown()) {
            Pile activePile = card.getContainingPile();
            if (activePile.getPileType() == Pile.PileType.STOCK)
                return;
            double offsetX = e.getSceneX() - dragStartX;
            double offsetY = e.getSceneY() - dragStartY;

            draggedCards.clear();
            List<Card> cards = activePile.getCards();
            int cardIndex = cards.indexOf(card);
            for(int i=cardIndex; i < cards.size(); i++){
                Card item = cards.get(i);
                draggedCards.add(item);
                item.getDropShadow().setRadius(20);
                item.getDropShadow().setOffsetX(10);
                item.getDropShadow().setOffsetY(10);

                item.toFront();
                item.setTranslateX(offsetX);
                item.setTranslateY(offsetY);
            }



        }
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        Card card = (Card) e.getSource();
        Pile tableauPile = getValidIntersectingPile(card, tableauPiles);
        Pile foundationPile = getValidIntersectingPile(card, foundationPiles);
        //TODO

        isMoveValid(card, foundationPile){

        }
            //handleValidMove(card, tableauPile);
        draggedCards.forEach(MouseUtil::slideBack);

        draggedCards.clear();
    };

    public boolean isGameWon() {
        //TODO
        return false;
    }

    public Game() {
        deck = Card.createNewDeck();

        initPiles();
        dealCards();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        List<Card> discardPileCards =discardPile.getCards();
        Collections.reverse(discardPileCards);
        stockPile.clear();
        stockPile.addCards(discardPileCards);
        discardPile.clear();
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        Card topCard = destPile.getTopCard();
        Pile.PileType PileType = destPile.getPileType();
        if(PileType == Pile.PileType.FOUNDATION) {
            return Card.isRankValidFoundation(topCard, card);
        }
        if(PileType == Pile.PileType.TABLEAU) {
            return Card.isRankValidTableau(topCard, card);
        }
        else {
            return false;
        }
    }

    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty()) {
            if (isValidMoveToEmptyPile(card)) {
                System.out.println("isoverpile empty :  " + card.getBoundsInParent().intersects(pile.getBoundsInParent()));
                return card.getBoundsInParent().intersects(pile.getBoundsInParent());
            }
            return false;
        } else {
            System.out.println("isoverpile else ág:  " + card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent()));
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
        }
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
    }


    private void initPiles() {
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }

    public void dealCards() {
        Iterator<Card> deckIterator = deck.iterator();
        Iterator<Pile> tableIterator = tableauPiles.iterator();
        int tableSize = 1;
        for(int pileNumber = 1; pileNumber <= 7; pileNumber++){
            Pile table = tableIterator.next();
            for(int i = 1; i <= tableSize; i++){
                Card card = deckIterator.next();
                table.addCard(card);
                addMouseEventHandlers(card);
                if (i == tableSize){
                    card.flip();
                }
                getChildren().add(card);
            }
            tableSize++;
        }
        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }
    public Game newGame() {
        deck.clear();
        return new Game();
    }

    private boolean isValidMoveToEmptyPile(Card card) {
        //create only rank from cardName
        String cardName = card.toString();
        String[] cardNameArray = cardName.split(";");
        String cardRank = cardNameArray[1];

        String king = Card.CardRank.KING.toString();
        return cardRank.equals(king);
    }

}
