package com.codecool.klondike;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.*;

public class Card extends ImageView {

    private String suit;
    private String rank;
    private boolean faceDown;

    private Image backFace;
    private Image frontFace;
    private Pile containingPile;
    private DropShadow dropShadow;

    static Image cardBackImage;
    private static final Map<String, Image> cardFaceImages = new HashMap<>();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 215;

    public Card(String suit, String rank, boolean faceDown) {
        this.suit = suit;
        this.rank = rank;
        this.faceDown = faceDown;
        this.dropShadow = new DropShadow(2, Color.gray(0, 0.75));
        backFace = cardBackImage;
        frontFace = cardFaceImages.get(getShortName());
        //String shortName = "S" + suit + "R" + rank;
        //frontFace = cardFaceImages.get(shortName);
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    /*public CardSuit getSuit() {
        return suit;
    }

    public CardRank getRank() {
        return rank;
    }
    */

    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        //"S" + suitName + "R" + cardRank
        System.out.println("get short name: " + suit + rank);
        return suit + rank;
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

    public static boolean isSameSuit(String card1, String card2) {
        //return card1.getSuit().equals(card2.getSuit());
        return card1.equals(card2);
    }

    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();

        //set up result with ordered cards
        String[][] randomisedCards = createRandomisedCards();
        String[] cardNamesDetailed = randomisedCards[2];

        for (String cardName : cardNamesDetailed) {
            String[] cardFullname = cardName.split(";");
            String suit = cardFullname[0];
            String rank = cardFullname[1];
            result.add(new Card(suit, rank, true));

        }

        /*for (CardSuit suit : CardSuit.values()) {
            for (CardRank rank : CardRank.values()) {
                result.add(new Card(suit, rank, true));
            }
        }*/
        return result;
    }

    private static String[][] createOrderedCards() {
        String suitName = "";
        String[][] orderedCards = new String[3][52]; //1st for cardNames, 2nd for cardIds
        int counter = 0;

        //List<String> cardNamesOrdered = new LinkedList<>();
        //List<String> cardIdsOrdered = new LinkedList<>();


        for (CardSuit suit : CardSuit.values()) {
            suitName = suit.getName();
            for (CardRank rank : CardRank.values()) {
                int cardRank = rank.getNumber();
                String cardName = suitName + cardRank;
                String cardNameDetailed = suitName + ";" + cardRank;
                //System.out.println(cardName);
                orderedCards[0][counter] = cardName;
                orderedCards[1][counter] = "S" + suit + "R" + rank;
                orderedCards[2][counter] = cardNameDetailed;
                counter += 1;

                //cardNamesOrdered.add(suitName + cardRank);
                //cardIdsOrdered.add("S" + suit + "R" + rank);
                //String cardId = "S" + suit + "R" + rank;
                //System.out.println("cardid: " + cardId);
            }
        }
        return orderedCards;
    }

    private static String[][] createRandomisedCards() {
        String[][] orderedCards = createOrderedCards();
        String[] cardNamesOrdered = orderedCards[0];
        String[] cardIdsOrdered = orderedCards[1];
        String[] cardNamesOrderedDetailed = orderedCards[2];

        String[][] randomisedCards = new String[3][52];
        //System.out.println(cardNamesOrdered);
        //System.out.println(cardIdsOrdered);

        //randomising cards
        List<String> cardNames = new LinkedList<>();
        List<String> cardIds = new LinkedList<>();
        List<String> cardNamesDetailed = new LinkedList<>();
        for (int i = 0; i < cardNamesOrdered.length; i++) {
            while (true) {
                Random rand = new Random();
                int random = rand.nextInt(cardNamesOrdered.length);
                if (!cardNames.contains(cardNamesOrdered[random])) {
                    cardNames.add(cardNamesOrdered[random]);
                    cardIds.add(cardIdsOrdered[random]);
                    cardNamesDetailed.add(cardNamesOrderedDetailed[random]);

                    /*String cardName = cardNamesOrdered.get(random);
                    String cardId = cardIdsOrdered.get(random);
                    String imageFileName = "card_images/" + cardName + ".png";
                    cardFaceImages.put(cardId, new Image(imageFileName));*/
                    break;
                }
            }
        }
        randomisedCards[0] = cardNames.toArray(new String[cardNames.size()]);
        randomisedCards[1] = cardIds.toArray(new String[cardIds.size()]);
        randomisedCards[2] = cardNamesDetailed.toArray(new String[cardNamesDetailed.size()]);

        return randomisedCards;
    }


    public static void loadCardImages() {
        cardBackImage = new Image("card_images/card_back.png");

        String[][] randomisedCards = createRandomisedCards();
        String[] cardNames = randomisedCards[0];
        String[] cardIds = randomisedCards[1];

        for (int i = 0; i < cardNames.length; i++) {
            String cardName = cardNames[i];
            String imageFileName = "card_images/" + cardName + ".png";
            String cardId = cardIds[i];
            System.out.println("cardname: " + cardName);
            System.out.println("cardid: " + cardId);
            cardFaceImages.put(cardId, new Image(imageFileName));
            System.out.println(cardId);
            System.out.println(cardName);
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

        public int getNumber() {
            return ordinal() + 1;
        }
    }
}
