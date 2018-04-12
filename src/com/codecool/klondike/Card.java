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
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        //"S" + suitName + "R" + cardRank
        System.out.println("get short name: " + suit + rank);
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
        String diamonds = CardSuit.DIAMONDS.toString();
        String hearts = CardSuit.HEARTS.toString();
        String clubs = CardSuit.CLUBS.toString();
        String spades = CardSuit.SPADES.toString();
        if(((card1.suit == hearts) || (card1.suit == diamonds)) && ((card2.suit == clubs) || (card2.suit == spades))){
            return true;
        }
        if(((card2.suit == hearts) || (card2.suit == diamonds)) && ((card1.suit == clubs) || (card1.suit == spades))){
            return true;
        }
        return false;
    }

    public static boolean isSameSuit(Card card1, Card card2) {
        //return card1.getSuit().equals(card2.getSuit());
        /*
        OR:
        String suit1 = card1.suit;
        String suit2 = card2.suit;
        return suit1.equals(suit2);
         */
        return card1.suit.equals(card2.suit);
    }

    public static boolean isRankValidTableau(Card card1, Card card2) {
        int card1Rank = 0;
        int card2Rank = 0;

        for (CardRank rank : CardRank.values()) {
            String cardRankDetailed = rank.getRankString();
            if(cardRankDetailed == card1.rank) {
                card1Rank = rank.getRankNumber();
            }
            if(cardRankDetailed == card2.rank) {
                card2Rank = rank.getRankNumber();
            }
        }
        if((card1Rank -1) == card2Rank) {
            return true;
        }
        else {
            return false;
        }
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
        return result;
    }

    private static String[][] createOrderedCards() {
        String suitName = "";
        String suitNameDetailed;
        String[][] orderedCards = new String[3][52]; //1st for cardNames, 2nd for cardIds
        int counter = 0;

        for (CardSuit suit : CardSuit.values()) {
            suitName = suit.getSuitName();
            suitNameDetailed = suit.getSuitNameDetailed();
            for (CardRank rank : CardRank.values()) {
                int cardRank = rank.getRankNumber();
                String cardRankDetailed = rank.getRankString();

                String cardName = suitName + cardRank;
                String cardNameDetailed = suitNameDetailed + ";" + cardRankDetailed;
                orderedCards[0][counter] = cardName;
                orderedCards[1][counter] = "S" + suit + "R" + rank;
                orderedCards[2][counter] = cardNameDetailed;
                counter += 1;
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

        public String getSuitName() {
            String suitname = this.toString();
            suitname = suitname.toLowerCase();
            return suitname;
        }
        public String getSuitNameDetailed() {
            return this.toString();

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

        public int getRankNumber() {
            return ordinal() + 1;
        }
        public String getRankString() {
            return this.toString();
        }
    }
}
