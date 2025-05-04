import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.stream.Stream;
import java.awt.event.ItemEvent;
import javax.swing.Icon;
import java.util.concurrent.TimeUnit;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.sql.Time;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.Font;
//Playing card images from https://opengameart.org/content/playing-cards-vector-png
public class game {
    

    final private static String[] cardNames = {"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King"};
    final private static String[] suitNames = {"Heart","Spade","Diamond","Club"};
    final private static String[] phases = {"Select Crib","Play","Score Hand",};
     private static int currentPhase = 0;
     private static int playerScore =0;
     private static int opponentScore = 0;

     private static ArrayList<Card> Deck = createCards();
    final private static ArrayList<Card> Hand = new ArrayList<Card>();
    final private static ArrayList<Card> OHand = new ArrayList<Card>();
    final private static ArrayList<Card> Crib = new ArrayList<Card>();
    final private static ArrayList<JLabel> panelMarkers = new ArrayList<JLabel>();
    final private static ArrayList<Card> OHandStorage = new ArrayList<Card>();
    final private static ArrayList<Card> PHandStorage = new ArrayList<Card>();
     private static Card topOfDeck;
    final private static JCheckBox[] handMarkers = new JCheckBox[6];
    final  static Memory memory = new Memory();
     private static boolean dealer = false;
     private static boolean won = false;
    
    
    public static void main(String[] args) throws IOException, InterruptedException{
        
        
        JFrame frame = new JFrame("Cribbage");
        memory.setMainFrame(frame);
        JLabel badInput = new JLabel("Red = bad input");
        JLabel instructionLabel = new JLabel("Pick two cards to put in the crib");
        instructionLabel.setBounds(0,0,700,20);
        badInput.setBounds(0,360,700,50);
        JLabel opponentInfo = new JLabel("");
        opponentInfo.setBounds(0,200,700,100);
        JLabel playScore = new JLabel("Total:0");
        playScore.setBounds(625,375,100,50);
        frame.add(opponentInfo);
        frame.add(badInput);
        frame.add(instructionLabel);
        frame.add(playScore);
        memory.setPlayCounter(playScore);
        memory.setInstructionLabel(instructionLabel);
        memory.setOpponentActions(opponentInfo);
        memory.setPlayerActions(badInput);
       
        // Creates the frame
        
        // adding the action listener
        //ActionListener listener = new ClickListener();
        frame.setSize(700,700);
        JButton button = new JButton("Submit");
        memory.setSubmitButton(button);
        
        button.setBounds(250,400,200,50);
        //button.addActionListener(listener);
        JLabel playerScore = new JLabel("0");
        JLabel opponentScore = new JLabel("0");
        playerScore.setBounds(0,325,100,50);
        opponentScore.setBounds(0,275,100,50);
        frame.add(playerScore);
        frame.add(opponentScore);
        memory.setPlayerScore(playerScore);
        memory.setOpponentScore(opponentScore);
        JPanel panel = new JPanel();
        memory.setHandPanel(panel);
        panel.setLayout(new GridLayout(2,3));
        //Code to make the the part where it show the top card of the deck
        JLabel showTopOfDeck = new JLabel("Top of Deck");
        showTopOfDeck.setBounds(0,0,300,150);
        memory.getMainFrame().add(showTopOfDeck);
        memory.setTopOfDeckShow(showTopOfDeck);


        showCards(Deck,panel);
        frame.setLayout(null);

        
        
        panel.setBounds(175, 450, 400, 200);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               submit(); }});
        JPanel playPanel = new JPanel();
        playPanel.setLayout(new GridLayout(1,8));
        memory.setPlayPanel(playPanel);
        playPanel.setBounds(0,275,700,100);
        frame.add(playPanel);
        JPanel oHandPanel = new JPanel();
        oHandPanel.setLayout(new GridLayout(1,4));
        memory.setOHandPanel(oHandPanel);
        oHandPanel.setBounds(125,125,450,100);
        JLabel info =new JLabel("Opponents Hand");
        frame.add(oHandPanel);
        info.setBounds(0,150,200,50);
        memory.setoHandSignifier(info);
        frame.add(info);


        
        frame.add(panel);
        frame.add(button);
        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //5. Show it.
       frame.setVisible(true);
       
       //Determining the dealer first
        Random r = new Random();
        int flip = r.nextInt(0,2);
        String dealStatement = "Opponent's deal";
        if(flip==1){
            dealer = true;
             dealStatement = "Your deal";
        }

        JLabel deal = new JLabel(dealStatement);
        deal.setBounds(0,550,200,50);
        frame.add(deal);
        memory.setDealSignifier(deal);
        refresh();


    }
        
   

    /**
     * This method instantiates a full 52 card deck of cards and assigns it to an Array List which it returns
     * @return  ArrayList<Card> The list that is filled with the cards that are created
     */
 
    private static ArrayList<Card> createCards(){
        ArrayList<Card> deck = new ArrayList<>();
        for(int i =0;i<52;i++){
            int cardOn= i/4;
            int modular = i%4;
            String suit= suitNames[modular];
            if(cardOn<9){
            deck.add(new Card(cardOn+1,cardNames[cardOn],suit));
            }
            else{
                deck.add(new Card( 10,cardNames[cardOn],suit));
            }
           
        }
        return deck;
    }

/**
 * This method finds the images for the first 5 cards in the deck and then sticks them to labels, puts them in a panel and then returns that panel
 * @param Deck The pool of cards that this method draws from
 * @param panel The panel that I am affixing the labels and the cards to
 * @throws IOException An exception that might be thrown
 */
    public static void showCards(ArrayList<Card> Deck,JPanel panel) 
{
    for (int i =0;i<20;i++){
        shuffle(Deck);
    }
    ArrayList<Card> cards = new ArrayList<Card>();
    for(int i=0;i<6;i++){
        cards.add(Deck.get(i));
    }
    for(int i=5; i>-1;i--){
        Deck.remove(i);
    }
    populateHand(cards,0);
    for(int i =0;i<6;i++){
        if(i<4){
            OHand.add(Deck.get(0));
            Deck.remove(0);
        }
        else{
            Crib.add(Deck.get(0));
            Deck.remove(0);
        }
    }
    topOfDeck = Deck.get(0);
    Deck.remove(0);
    memory.getInstructionLabel().setText("Pick two cards to put in the crib");
    refresh();
    System.out.println(OHand);
}
/**
 * This method fill the hand with new JLabels from the list 
 * @param toAdd The list of cards to put int the hand
 * @param startAt What position the populate function will start puttin cards at
 */
public static void populateHand(ArrayList<Card> toAdd,int startAt){
   if (toAdd.size()+Hand.size()<=6){
    for(int i = 0; i<toAdd.size();i++){
        Card current = toAdd.get(i);
        try {
        JCheckBox temp = new JCheckBox(new ImageIcon(current.getImage()));
        cardListener s = new cardListener();
        temp.addItemListener(s);
        handMarkers[i+startAt] = temp;
        memory.getHandPanel().add(temp);
        Hand.add(current);
        refresh();
       
        
        }
        catch(Exception e){System.out.println("This messed up");}
    }
}
else {
    throw new NullPointerException("Too many cards in hand");
}
}

public static void submit() {
    if(won){
        return;
    }
    
    if(currentPhase == 0){
        dealToCrib();
        if(currentPhase == 1){
        if(dealer){
            playCard(false,false);
            if(topOfDeck.getRank().contentEquals("Jack")){
                memory.getPlayerActions().setText("Jack on Top-2");
                playerScore+=2;
                memory.getPlayerScore().setText(""+playerScore+"(+2)");
            }
        }
        else{
            if(topOfDeck.getRank().contentEquals("Jack")){
                memory.getOppnentActions().setText("Jack on Top-2");
                opponentScore+=2;
                memory.getOpponentScore().setText(""+opponentScore+"(+2)");
            }
        }
        ArrayList<JLabel> marks = memory.getOHandMarkers();
        JPanel oHandPanel = memory.getoHandPanel();
        for(JLabel i : marks){
            oHandPanel.remove(i);
            
        }
        marks.clear();
       
    }
        
    }
    else if (currentPhase == 1){
        playCard(true,false);
        if (currentPhase==2){
            memory.getPlayerActions().setText("");
            if(dealer){
                memory.getOppnentActions().setText("Points from Hand: ");
                memory.getOpponentScore().setText(""+opponentScore+"(+"+scoreOpponent(OHand)+")");
                
            }
        }
    }
    else{
        countCards(true,true);
    
    }
    

}
/**
 * This method counts the number of cards that the player currently has selected in their hand
 * @return int The number of cards that the player currently has selected
 */
public static int countSelected(){
    JPanel hand = memory.getHandPanel();
    JFrame frame = memory.getMainFrame();
    int numberSelected = 0;
    for(JCheckBox i : handMarkers){
    if(i.isSelected()){
        numberSelected +=1;
    }
    
}
return numberSelected;
}
public static void dealToCrib() {
    
    JPanel hand = memory.getHandPanel();
    JFrame frame = memory.getMainFrame();
    

    if (countSelected() != 2){
       incorrectInput();}
    else{
        int[] toFix= new int[2];
        int toFixOn =0;
       for(int i=0;i<6; i++){
        if(handMarkers[i].isSelected()){
            Crib.add(Hand.get(i));
            toFix[toFixOn] = i;
            toFixOn ++;
        }
       }
       int offSet = 0;
       for (int i:toFix){
        hand.remove(handMarkers[i-offSet]);
        Hand.remove(i-offSet);
        resetHandMarkers(i-offSet);
        offSet++;
       }
       currentPhase = 1;
       JLabel label = memory.getInstructionLabel();
       label.setText("This is the play phase. Pick a card to play");
       try{
       memory.getSetTopOfDeckShow().setIcon(new ImageIcon(topOfDeck.getImage()));
       }
       catch(Exception E){

       }

       refresh();
    }
}
public static  void resetHandMarkers(int position){
    for(int i=position;i<6;i++){
        if(i!=5){
        handMarkers[i]=handMarkers[i+1];
        }
        else{
            handMarkers[i]=new JCheckBox();
        }
    }

}
public static void refresh(){
    
    
    if(playerScore>=121 | opponentScore>=121){
        won = true;
        String message = "You Lost!";
        if(playerScore >opponentScore){
            message = "You Won!";
        }
        JLabel winMessage = new JLabel(message +" Please Exit the program");

        winMessage.setFont(new Font("Serif", Font.PLAIN, 50));
        winMessage.setBounds(0,0,700,700);
        JFrame frame= memory.getMainFrame();
        frame.add(winMessage);
        frame.remove(memory.getPlayPanel());


        
    }
    SwingUtilities.updateComponentTreeUI(memory.getMainFrame());

}
public static void playCard(boolean player,boolean playerGO){

    List<Card> played = memory.getPlayCards();
    int total = 0;
    for(Card i : played){
        total += i.getValue();
    }
    JPanel panel  = memory.getPlayPanel();
    JPanel oHandPanel = memory.getoHandPanel();
    Card cardToPlay = new Card(5,"","");


    if(player){
       
        if(countSelected()!=1){
            incorrectInput();
            return;
        }

        
        for (int i =0; i<Hand.size();i++){
            if(handMarkers[i].isSelected()){
                cardToPlay = Hand.get(i);
                total += cardToPlay.getValue();
                if(total >31){
                    incorrectInput();
                    memory.getPlayerActions().setText("That is over 31 and cannot be submited");
                    return;
                }
                Hand.remove(i);
                PHandStorage.add(cardToPlay);
                memory.getHandPanel().remove(handMarkers[i]);
                resetHandMarkers(i);
                refresh();;
                
                break;
            }}
        played.add(cardToPlay);
        try{JLabel cardI = new JLabel(new ImageIcon(cardToPlay.getImage()));
            panel.add(cardI);
            panelMarkers.add(cardI);
            System.out.println("Successfully add "+cardToPlay);}
        catch(Exception E){
            System.out.println("Failed to add"+cardToPlay);
         }
         memory.getPlayCounter().setText("Total:"+total);
         refresh();
        checkPlayScore(true,true); 
        }
        
    
    //oponents turn
    boolean oGo = false;
    int bestCard = -1;
    int bestScore =-1;
    for(int i=0;i<OHand.size();i++){
       if(OHand.get(i).getValue()+total<=31){
        played.add(OHand.get(i));
        //System.out.println("Trying "+OHand.get(i));
        if (checkPlayScore(false,false)>bestScore){
            bestCard=i;
        }
        played.remove(played.size()-1);
        }
    }
        if( bestCard != -1){
            cardToPlay = OHand.get(bestCard);
            total += cardToPlay.getValue();
            OHand.remove(bestCard);
            OHandStorage.add(cardToPlay);
            played.add(cardToPlay);
            memory.getOHandSignifier().setText("Opponent Hand");
        System.out.println("Printing "+cardToPlay);
        try{// mid level image
            JLabel cardI = new JLabel(new ImageIcon(cardToPlay.getImage()));
            panel.add(cardI);
            panelMarkers.add(cardI);
            //top level image
            JLabel newCard = new JLabel(new ImageIcon(cardToPlay.getImage()));
            oHandPanel.add(newCard);
            memory.getOHandMarkers().add(newCard);
            System.out.println(memory.getOHandMarkers());
            refresh();
        }
        catch(Exception E){
            System.out.println("Failed to add "+cardToPlay);
         }
         memory.getPlayCounter().setText("Total:"+total);
         
        checkPlayScore(false,true);
        }
            
        else{
            memory.getOppnentActions().setText(memory.getOppnentActions().getText()+" GO-stops playing");
            oGo = true;
            if(!playerGO){
            playerScore+=1;
            memory.getPlayerScore().setText(""+playerScore);
            memory.getPlayerActions().setText(memory.getPlayerActions().getText()+" Last Card-1");
            }
        }
        
        if (!playerGO){
            boolean canPlay = false;
            for(Card i :Hand){
                if(i.getValue()+total<=31){
                    canPlay=true;
                }
            }
            if(!canPlay){
                memory.getPlayerActions().setText(memory.getPlayerActions().getText() +" GO-can't play");
               if(!oGo){
               opponentScore+=1;
               memory.getOpponentScore().setText(""+opponentScore);
               memory.getOppnentActions().setText(memory.getOppnentActions().getText()+" Last Card-1");
                }
               playerGO = true;
            }
        }
    
    
        refresh();
    if(playerGO && oGo){
        played.clear();
        for(JLabel i : panelMarkers){
          panel.remove(i);
        }
        if(Hand.isEmpty()&& !OHand.isEmpty()){
            playCard(false,true);
            return;
        }
    }
        if(playerGO && !oGo){
        playCard(false,true);
        return;
    }


    if(OHand.isEmpty() && Hand.isEmpty()){
        currentPhase = 2;
        for(Card i : OHandStorage){
            OHand.add(i);
        }
        OHand.add(topOfDeck);
        PHandStorage.add(topOfDeck);
        populateHand(PHandStorage,0);
        
        OHandStorage.clear();
        PHandStorage.clear();
        memory.getInstructionLabel().setText("This is the scoring phase. Choose scoring combinations of cards. (If you select nothing you will end the round)");
        refresh();
    }
}
    





public static int checkPlayScore(boolean player,boolean update){
    
    List<Card> inPlay = memory.getPlayCards();
    int ScoreToAdd = 0;
    String Scores = "";
    int end = inPlay.size()-1;
    Card cardOnTop = inPlay.get(end);
    if(inPlay.size()<2){
        return 0;
    }
    //Matches
    if(cardOnTop.getRank()==inPlay.get(end-1).getRank()){
        String type = "Pair-2\n";
        ScoreToAdd +=2;
        if(inPlay.size()>2){
        if(cardOnTop.getRank() == inPlay.get(end-2).getRank()){
            type =  "Three of a kind-6\n";
            ScoreToAdd+=4;
            if(inPlay.size()>3){
            if (cardOnTop.getRank()== inPlay.get(end-3).getRank()){
                type = "Four of a kind-12\n" ;
                ScoreToAdd+=6;
            }
        }}}
        Scores += type;
    }
    // Checks 15 and 31
        int total = 0;
        for(Card i : inPlay){
            total+=i.getValue();
        }
        if(total == 15){
            Scores += "15-2\n";
            ScoreToAdd+=2;
        }
        if(total == 31){
            Scores += "31-2\n";
           ScoreToAdd +=2;
        }
    //Checks for a run
    if(!(Scores.contains("Pair")&&Scores.contains("Three of a kind")&&Scores.contains("Four of a kind"))){
        List order = Arrays.asList(cardNames);
        int topPosition = order.indexOf(cardOnTop.getRank());
        int secondPosition = order.indexOf(inPlay.get(end-1).getRank());
        ArrayList<Integer> positions = new ArrayList<Integer>();
        positions.add(topPosition);
        positions.add(secondPosition);
        if(Math.abs(topPosition-secondPosition)<8){
        int runLength = 0;
        for(int i=end-2;i>-1;i--){
            
            positions.add(order.indexOf(inPlay.get(i).getRank()));
            positions.sort(null);
            boolean run = true;
            for(int j=0;j<positions.size()-1;j++){
                if(positions.get(j)+1 != positions.get(j+1)){
                    run=false;
                }
            }
            if(run){
                runLength = positions.size();
            }


        }
        if(runLength!=0){
            ScoreToAdd+=runLength;
            Scores+="Run of "+runLength+"-"+runLength;
        }
    }
    }
    //Checks for a flush
    if( inPlay.size() >= 4){
        int suitInARow = 1;
        String suit = cardOnTop.getSuit();
        for(int i = end-1; i>0;i--){
            if(inPlay.get(i).getSuit().contains(suit)){
                suitInARow+=1;
            }
            else{
                break;
            }
        }
        if(suitInARow>=4){
            ScoreToAdd+=suitInARow;
            Scores += "Flush-"+suitInARow+"\n";
        }
    }
    if (update){
        if (player){
            playerScore+=ScoreToAdd;
            memory.getPlayerScore().setText(""+playerScore);
            memory.getPlayerActions().setText(Scores);
            System.out.println(Scores);
        }
        else{
            opponentScore += ScoreToAdd;
            memory.getOpponentScore().setText(""+opponentScore);
            memory.getOppnentActions().setText(Scores);
        }
    refresh();
    return ScoreToAdd;
    }
    else{
        return ScoreToAdd;
    }

    


}

public static int countCards(boolean player, boolean update){
    ArrayList<Card> toScore = new ArrayList<Card>();    
        if (countSelected()==0){
       wrapUpRound();
        return 0;
    }
    for(int i=0;i<5;i++){
        if(handMarkers[i].isSelected()){
            toScore.add(Hand.get(i));
        }
    }

    toScore.sort( new cardCompare());
    System.out.println(toScore);
    if(memory.getScoredHands().contains(toScore)){
        if(player){
        incorrectInput();
        memory.getPlayerActions().setText("You have already submitted that hand");
    }
        return 0;
    }
    int scoretoAdd = 0;
    String Scores = " ";
    int size = toScore.size();
    
    // checks to see if there is a jack that matches suit of the top card
    if(size == 1){
        if(toScore.get(0).getRank().contentEquals("Jack") && toScore.get(0).getSuit().contentEquals(topOfDeck.getSuit()) && topOfDeck != toScore.get(0)){
            Scores += "Nobs-1 ";
            scoretoAdd +=1;
        }
    }
    // Checks to see if there are matches
    String rank =toScore.get(0).getRank();
    boolean allSame = true;
    for(Card i :toScore){
        if(i.getRank()!=rank){
            allSame = false;
        }
    }
 
    if(allSame){
        if(size == 2){
            Scores += "Pair-2 ";
            scoretoAdd +=2;
        }
        else if(size == 3){
            int toRun = makePairingsForThree(toScore);
            for(int i =0; i<toRun;i++){
            Scores += "Pair-2 ";
            scoretoAdd+=2;
            }
            
        }
        else if(size == 4){
            int toRun = makePairingsForFour(toScore);
            for(int i =0; i<toRun;i++){
            Scores += "Pair-2 ";
            scoretoAdd+=2;
            }

    }
    }

    //Checks for a 15

    int total = 0;
    for(Card i : toScore){
        total += i.getValue();
    }
    if(total == 15){
        scoretoAdd +=2;
        Scores += "15-2 ";
    }

    // Checks for a flush

    if(countSelected() >= 4){
        String suit = toScore.get(0).getSuit();
        allSame = true;
        for(Card i : toScore){
            if(i.getSuit() != suit){
                allSame = false;
                break;
            }
        }
        if (allSame){
            Scores += "Flush of " + size +"-"+size;
            scoretoAdd += size;
        }
    }

    //Checks for a run

    if(countSelected()>=3){
        boolean inARow = true;
       cardCompare c = new cardCompare();
        for( int i = 0; i<size-1; i++){
                if(c.compare(toScore.get(i+1),toScore.get(i))!=4){
                    inARow = false;
                    break;
                }
            }
        if (inARow){
            Scores+="Run of "+size +"-"+size;
            scoretoAdd += size;
        }
    }

    memory.getScoredHands().add(toScore);

    if (update){
        if (player){
            playerScore+=scoretoAdd;
            memory.getPlayerScore().setText(playerScore+ "(+"+scoretoAdd+")");
            memory.getPlayerActions().setText(memory.getPlayerActions().getText()+Scores);
            System.out.println(Scores);
        }
        else{
            opponentScore += scoretoAdd;
            memory.getOpponentScore().setText(opponentScore+ "(+"+scoretoAdd+")");
            memory.getOppnentActions().setText(memory.getOppnentActions().getText()+Scores);
        }
    refresh();
    return scoretoAdd;
    }
    else{
        return scoretoAdd;
    }
}
public static int countCards(ArrayList<Card> diff){
    ArrayList<Card> toScore = new ArrayList<Card>();
    for(Card i:diff){
        toScore.add(i);
    }
    toScore.sort(new cardCompare());
    if(memory.getScoredHands().contains(toScore)){
        return 0;
    }
    //System.out.println("Got through:"+toScore);
    int scoretoAdd = 0;
    String Scores = " ";
    int size = toScore.size();
    
    // Checks to see if there are matches
    String rank =toScore.get(0).getRank();
    boolean allSame = true;
    for(Card i :toScore){
        if(i.getRank()!=rank){
            allSame = false;
        }
    }
    if(size == 1){
        if(toScore.get(0).getRank().contentEquals("Jack") && toScore.get(0).getSuit().contentEquals(topOfDeck.getSuit()) && topOfDeck != toScore.get(0)){
            Scores += "Nobs-1 ";
            scoretoAdd +=1;
        }
    }
    if(allSame){
        if(size == 2){
            Scores += "Pair-2 ";
            scoretoAdd +=2;
        }
        else if(size == 3){
            int toRun = makePairingsForThree(toScore);
            for(int i =0; i<toRun;i++){
            Scores += "Pair-2 ";
            scoretoAdd+=2;
            }
            
        }
        else if(size == 4){
            int toRun = makePairingsForFour(toScore);
            for(int i =0; i<toRun;i++){
            Scores += "Pair-2 ";
            scoretoAdd+=2;
            }

    }
    }

    //Checks for a 15

    int total = 0;
    for(Card i : toScore){
        total += i.getValue();
    }
    if(total == 15){
        scoretoAdd +=2;
        Scores += "15-2 ";
    }

    // Checks for a flush

    if(size >= 4){
        String suit = toScore.get(0).getSuit();
        allSame = true;
        for(Card i : toScore){
            if(i.getSuit() != suit){
                allSame = false;
                break;
            }
        }
        if (allSame){
            Scores += "Flush of " + size +"-"+size+" ";
            scoretoAdd += size;
        }
    }

    //Checks for a run

    if(size>=3){
        boolean inARow = true;
       cardCompare c = new cardCompare();
        for( int i = 0; i<size-1; i++){
                if(c.compare(toScore.get(i+1),toScore.get(i))!=1){
                    inARow = false;
                    break;
                }
            }
        if (inARow){
            Scores+="Run of "+size +"-"+size+ " ";
            scoretoAdd += size;
        }
    }

    memory.getScoredHands().add(toScore);

    if(scoretoAdd>0){
        opponentScore += scoretoAdd;
        memory.getOpponentScore().setText(opponentScore+ "(+"+scoretoAdd+")");
        memory.getOppnentActions().setText(memory.getOppnentActions().getText()+Scores);
    }
        
    refresh();
    return scoretoAdd;
    
}

public static void wrapUpRound(){
    if(dealer)
    {
        if(!Crib.isEmpty()){
            for(Card i : Hand){
                Deck.add(i);
                
            }
            Deck.remove(topOfDeck);
            Hand.clear();
            for(int i =0; i<6;i++){
                memory.getHandPanel().remove(handMarkers[i]);
                handMarkers[i]= new JCheckBox();
            }
            Crib.add(topOfDeck);
            populateHand(Crib,0);
            Crib.clear();
            refresh();
            return;
        }
   
        cleanUp();
    }
    else{
        
        memory.getOppnentActions().setText("Points from Hand: ");
        int oHandScoreAdded = scoreOpponent(OHand);
        for(Card i :OHand){
            Deck.add(i);
        }
        OHand.clear();
        Deck.remove(topOfDeck);

        Crib.add(topOfDeck);
        memory.getOppnentActions().setText(memory.getOppnentActions().getText()+" Points from Crib: ");
        memory.getOpponentScore().setText(""+opponentScore+"(+"+oHandScoreAdded+")(+"+scoreOpponent(Crib)+")");
        ArrayList<JLabel> marks = memory.getOHandMarkers();
        JPanel oHandPanel = memory.getoHandPanel();
        for(JLabel i : marks){
            oHandPanel.remove(i);
            
        }
        marks.clear();
        for(Card i :Crib){
            try{
            JLabel temp = new JLabel(new ImageIcon(i.getImage()));
            oHandPanel.add(temp);
            marks.add(temp);
            }
            catch (Exception e){}
        }
        memory.getOHandSignifier().setText("Crib & Top Of Deck");
        refresh();
        cleanUp();
        
    }

}
public static void cleanUp(){
    if(dealer){


        dealer = false;
        memory.getDealSignifier().setText("Opponent's Deal");
        
    }
    else{
        dealer = true;
        memory.getDealSignifier().setText("Your deal");

    }

    Deck = createCards();
    Hand.clear();
    OHand.clear();
    Crib.clear();
    for(int i =0; i<6;i++){
        memory.getHandPanel().remove(handMarkers[i]);
        handMarkers[i]= new JCheckBox();
    }
    memory.getSetTopOfDeckShow().setIcon(new ImageIcon());
    showCards(Deck, memory.getHandPanel());
    refresh();
    currentPhase = 0;

}

public static int scoreOpponent(ArrayList<Card> hand){
    int totalscoreAdded = 0;
    for(int i = 0;i<5;i++){
        ArrayList<Card> currentList = new ArrayList<Card>();
        currentList.add(hand.get(i));
       totalscoreAdded+= countCards(currentList);
        for(int j=i+1;j<5;j++){
            currentList.add(hand.get(j));
            totalscoreAdded+= countCards(currentList);
            for(int h=j+1;h<5;h++){
                currentList.add(hand.get(h));
                totalscoreAdded+= countCards(currentList);
                for(int k=h+1;k<5;k++){
                    currentList.add(hand.get(k));
                    totalscoreAdded+=  countCards(currentList);
                    for(int l=k+1;l<5;l++){
                        currentList.add(hand.get(l));
                        totalscoreAdded+=  countCards(currentList);
                        currentList.remove(4);
                    }
                    currentList.remove(3);
                }
                currentList.remove(2);
            }
            currentList.remove(1);

        }

    }
    return totalscoreAdded;

}
public static ArrayList<Card> makeList(Card[] array){
    ArrayList<Card> toReturn = new ArrayList<Card>();
    for(Card i : array){
        toReturn.add(i);
    }
    return toReturn;
}

/**
 * This method takes a list that is 3 cards long and submits all combinations of 2 to Scored hands in memory
 * @param toScore The list that is 3 cards long
 */
public static int makePairingsForThree(ArrayList<Card> toScore){
   int pairs = 0;
    ArrayList<ArrayList<Card>> scored = memory.getScoredHands();
    Card[] temp = {toScore.get(0),toScore.get(1)};
    if(!scored.contains(makeList(temp))){
    scored.add(makeList(temp));
    pairs +=1;
    }
    Card[] temp1 = {toScore.get(0),toScore.get(2)};
    if(!scored.contains(makeList(temp1))){
    scored.add(makeList(temp1));
    pairs +=1;
    }
    Card[] temp2 = {toScore.get(1),toScore.get(2)};
    if(!scored.contains(makeList(temp2))){
    scored.add(makeList(temp2));
    pairs +=2;
    }
    return pairs;
}
/**
 * This method makes all combinations of 3 from a list of four adds them to the list of scored hands and then runs each of those combinations through the makePairingsForThree section
 * @param toScore The 4 long list of cards
 */
public static int makePairingsForFour(ArrayList<Card> toScore){
    int[] toRead = {0,1,2,0,1,3,0,2,3,1,2,3};
    int pairs = 0;
    for(int i = 0;i<4;i++){
        Card[] temp = {toScore.get(toRead[i*3]),toScore.get(toRead[i*3+1]),toScore.get(toRead[i*3+2])};
        if(!memory.getScoredHands().contains(makeList(temp))){
         pairs += makePairingsForThree(makeList(temp));
         memory.getScoredHands().add(makeList(temp));
        }
    }
    return pairs;

}

/**
 * This method flashes the button red for a half second to show that the player did something wrong
 * @throws InterruptedException
 */
public static void incorrectInput() {
    System.out.println("incorrect Input");
    JFrame frame = memory.getMainFrame();
   
    frame.setBackground(Color.RED);
   
    refresh();
    
    try{TimeUnit.MILLISECONDS.sleep(250);}
    catch (Exception e) {

    }
    
    frame.setBackground(Color.white);
    refresh();
        }




    /**
     * This method takes a list of cards and then returns that list but shuffled
     * @param deck The group of cards that is being shuffled
     * @return ArrayList<Card> The shuffled cards
     */
    private static ArrayList<Card> shuffle(ArrayList<Card> deck){
        Random generator = new Random();
        ArrayList<Card> tempDeck= new ArrayList<Card>();
        for(int i = 0; i<deck.size();i++){
            int position = generator.nextInt(0, deck.size());
            Card randCard = deck.get(position);
            tempDeck.add(randCard);
            deck.remove(randCard);
        }
        for(Card i:tempDeck){
            deck.add(i);
        }

        return deck;
    }


}




 class cardListener implements ItemListener{
   /**
    * This is the method that is called whenever a checkbox is clicked. It resized the cards so that the client can tell they are selected
    */
    public void itemStateChanged(ItemEvent e){
        Object temp = e.getSource();
        JCheckBox problem = (JCheckBox)temp;
       
            if (problem.isSelected()){
                ImageIcon temp1 =(ImageIcon)problem.getIcon();
                Image card = temp1.getImage().getScaledInstance(100, 120, Image.SCALE_DEFAULT);
                ImageIcon temp2 = new ImageIcon(card);
                problem.setIcon(temp2);
                 
                
            } 
            else{
                ImageIcon temp1 =(ImageIcon)problem.getIcon();
                Image card = temp1.getImage().getScaledInstance(80, 100, Image.SCALE_DEFAULT);
                ImageIcon temp2 = new ImageIcon(card);
                problem.setIcon(temp2);

            }

            
            }
    }


    
class cardCompare implements Comparator<Card>{
    final private static String[] cardNames = {"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King"};
    final private static String[] suits ={"Heart","Spade","Diamond","Club"};
    final private static List cardOrder = Arrays.asList(cardNames);
    final private static List suitNames = Arrays.asList(suits);
    public int compare(Card a,Card b){
        int aPosition = cardOrder.indexOf(a.getRank());
        int bPosition = cardOrder.indexOf(b.getRank());
        if(aPosition != bPosition){
        return 4*(aPosition-bPosition);
        }
        int aSuit = suitNames.indexOf(a.getSuit());
        int bSuit = suitNames.indexOf(b.getSuit());
        return aPosition-bPosition;


    }
}

 class Card{
// Variables that the object will hold
 private int value;
 private String rank;
 private String suit;
 private String imagePath;
 
/**
 * This method creates a card object
 * @param value The value of the card being created
 * @param rank The rank of the card being created
 * @param suit The suit of the card being created
 * @param imagePath Gives the name of the corresponding png file that houses the image of that card
 */
public Card(int value, String rank, String suit){
    this.value = value;
    this.rank = rank;
    this.suit = suit;
    

    if(Stream.of("Jack","King","Queen","Ace").anyMatch(i -> i.contentEquals(rank))){
        this.imagePath = "cardPictures/"+rank.toLowerCase()+"_of_"+suit.toLowerCase()+"s2.png";
    }
    else{
        this.imagePath = "cardPictures/"+value+"_of_"+suit.toLowerCase()+"s.png";
    }
}
/**
 * This method returns the rank of the card 
 * @return String the rank of the card
 */
 public String getRank(){
    return rank;
 }
/**
 * This mehod returns the value of the card
 * @return int The value of the card
 */
 public int getValue(){
    return value;
 }
 /**
 * This method returns the suit of the card 
 * @return String the suit of the card
 */
public String getSuit(){
    return suit;
 }
 public Image getImage() throws IOException{
        
        Image tempImage =new ImageIcon(imagePath).getImage();
        Image cardImage = tempImage.getScaledInstance(80, 100, Image.SCALE_DEFAULT);

       return cardImage;

 }

 /**
  * This method sets what object should return if it is turned into a string
  * @return String It will be of the form 5 of hearts
  */
 public String toString(){
    return rank+" of "+suit+"s";
 }

}
class Memory{
    private static JFrame mainFrame;
     private static JPanel handPanel;
     private static JButton submitButton;
     private static JLabel instructionLabel;
     private static JPanel playPanel;
     private static ArrayList<Card> playCards= new ArrayList<Card>();
     private static JLabel PlayerScore;
     private static JLabel opponentScore;
     private static JLabel opponentActions;
     private static JLabel playerActions;
     private static JLabel playCounter;
     private static JLabel showTopOfDeck;
     private static JPanel oHandPanel;
     private static ArrayList<JLabel> oHandPanelMarkers= new ArrayList<JLabel>();
     private static ArrayList<ArrayList<Card>> scoredHands = new ArrayList<ArrayList<Card>>();
     private static JLabel oHandSignifier ;
     private static JLabel dealSignifier;

/**
 * This method sets the main frame in the memory
 * @param frame the frame that you want to set
 */
    public static void setMainFrame(JFrame frame){
        mainFrame = frame;
    }
/**
 * This method sets the hand panel in memory
 * @param panel
 */
    public static void setHandPanel(JPanel panel){
        handPanel = panel;
    }
/**
 * This method sets the submit button in memory
 * @param button the submit button
 */
    public static void setSubmitButton(JButton button){
        submitButton = button;
    }
/**
 * This mehtod returns the main frame
 * @return
 */
    public static JFrame getMainFrame(){
        return mainFrame;
    }
/**
 * This method returns the hand panel
 * @return
 */
    public static JPanel getHandPanel(){
        return handPanel;
    }
/**
 * This method returns the submit button
 * @return
 */
    public static JButton getSubmitButton(){
        return submitButton;
    }
/**
 * This method sets the instruction label
 * @param label
 */
    public static void setInstructionLabel(JLabel label){
        instructionLabel = label;
    }
/**
 * This method returns the instructino label
 * @return
 */
    public static JLabel getInstructionLabel(){
        return instructionLabel;
    }
/**
 * This method sets the play panel
 */
public static void setPlayPanel(JPanel panel){
    playPanel =panel;
}
/**
 * This method returns the play panel
 * @return
 */
public static JPanel getPlayPanel(){
    return playPanel;
}

/**
 * This method gets the list of cards in the play zone
 * @return
 */
public static ArrayList<Card> getPlayCards(){
    return playCards;
}
/**
 * This method sets the label for the player score label
 * @param label
 */
public static void setPlayerScore(JLabel label){
    PlayerScore = label;
}
/**
 * This method returns the player score label
 * @return
 */
public static JLabel getPlayerScore(){
    return PlayerScore;
}
/**
 * This method sets the label for the opponent score label
 * @param label
 */
public static void setOpponentScore(JLabel label){
    opponentScore = label;
}
/**
 * This method returns the label for the opponent score label
 * @return
 */
public static JLabel getOpponentScore(){
    return opponentScore;
}
/**
 * This method sets the label for the opponent actions label
 * @param label
 */
public static void setOpponentActions(JLabel label){
    opponentActions = label;
}
/**
 * This method returns the label for the opponent actions label
 * @return
 */
public static JLabel getOppnentActions(){
    return opponentActions;
}
/**
 * This method sets the label for the player actions label
 * @param label
 */
public static void setPlayerActions(JLabel label){
    playerActions = label;
}
/**
 * This method returns the label for the player actions label
 * @return
 */
public static JLabel getPlayerActions(){
    return playerActions;
}
/**
 * This method sets the label for the play score counter label
 * @param label
 */
public static void setPlayCounter(JLabel label){
    playCounter = label;
}
/**
 * This method returns the label for the top of deck shower
 * @return
 */
public static JLabel getPlayCounter(){
    return playCounter;
}
public static void setTopOfDeckShow(JLabel label){
    showTopOfDeck = label;
}
/**
 * This method returns the label for the top of deck shower
 * @return
 */
public static JLabel getSetTopOfDeckShow(){
    return showTopOfDeck;
}
/**
 * This method sets the hand panel in memory
 * @param panel
 */
public static void setOHandPanel(JPanel panel){
    oHandPanel = panel;
}
/**
 * This method returns the panel for the opponents hand
 * @return
 */
public static JPanel getoHandPanel(){
    return oHandPanel;
}
/**
 * This method returns the list containt the markers for the opponents hand panel
 * @return
 */
public static ArrayList<JLabel> getOHandMarkers(){
    return oHandPanelMarkers;
}

/**
 * This method returns the lists of the lists of scored hands
 * @return
 */
public static ArrayList<ArrayList<Card>> getScoredHands(){
    return scoredHands;
}
/**
 * This method sets the variable for the label for the oponents hand
 * @return
 */
public static void setoHandSignifier(JLabel label){
    oHandSignifier = label;
}
/**
 * This method returns the label for the opponents hand
 * @return
 */
public static JLabel getOHandSignifier(){
    return oHandSignifier;
}
/**
 * Sets the label for who has the deal
 * @param label
 */
public static void setDealSignifier(JLabel label){
    dealSignifier = label;
}
/**
 * This method returns the label for who has the deal
 * @return
 */
public static JLabel getDealSignifier(){
    return dealSignifier;
}



    

}