package com.codecool.klondike;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.*;

public class Card extends ImageView {

    private CardSuit suit;
    private CardRank rank;
    private boolean faceDown;

    private Image backFace;
    private Image frontFace;
    private Pile containingPile;
    private DropShadow dropShadow;

    static Image cardBackImage;
    private static final Map<String, Image> cardFaceImages = new HashMap<>();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 215;

    public Card(CardSuit suit, CardRank rank, boolean faceDown) {
        this.suit = suit;
        this.rank = rank;
        this.faceDown = faceDown;
        this.dropShadow = new DropShadow(2, Color.gray(0, 0.75));
        backFace = cardBackImage;
        frontFace = cardFaceImages.get(getShortName());
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    public CardSuit getSuit() {
        return suit;
    }

    public CardRank getRank() {
        return rank;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        //"S" + suitName + "R" + cardRank
        return "S" + suit + "R" + rank;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }

    public Pile getContainingPile() {
        return containingPile;
    }

    public void setContainingPile(Pile containingPile) {
        this.containingPile = containingPile;
    }

    public void moveToPile(Pile destPile) {
        this.getContainingPile().getCards().remove(this);
        destPile.addCard(this);
    }

    public void flip() {
        faceDown = !faceDown;
        setImage(faceDown ? backFace : frontFace);
    }

    @Override
    public String toString() {
        return "The " + "Rank" + rank + " of " + "Suit" + suit;
    }

    public static boolean isOppositeColor(Card card1, Card card2) {
        //TODO
        return true;
    }

    public static boolean isSameSuit(Card card1, Card card2) {
        return card1.getSuit().equals(card2.getSuit());
    }

    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();
        for (CardSuit suit : CardSuit.values()) {
            for (CardRank rank : CardRank.values()) {
                result.add(new Card(suit, rank, true));
            }
        }
        return result;
    }

    public static void loadCardImages() {
        cardBackImage = new Image("card_images/card_back.png");
        String suitName = "";
        for (CardSuit suit : CardSuit.values()) {
            suitName = suit.getName();
            for (CardRank rank : CardRank.values()) {
                int cardRank = rank.getNumber();
                String cardName = suitName + cardRank;
                System.out.println(cardName);
                String cardId = "S" + suit + "R" + rank;
                System.out.println("cardid: " + cardId);
                String imageFileName = "card_images/" + cardName + ".png";
                cardFaceImages.put(cardId, new Image(imageFileName));
                }
            }
        }
    public enum CardSuit {
        HEARTS,
        DIAMONDS,
        SPADES,
        CLUBS;

        public String getName() {
            String suitname = this.toString();
            suitname = suitname.toLowerCase();
            return suitname;
            /*
            for (CardSuit suit : CardSuit.values()) {
                String suitname = suit.toString();
                suitname.toLowerCase();
                System.out.println(suitname);
            } */

        }
    }

    public enum CardRank {
        ACES,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JUMBO,
        QUEEN,
        KING;

       public int getNumber(){
            return ordinal()+1;
       }
    }
}
