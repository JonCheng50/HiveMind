import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question implements Serializable {
    private String text;
    private int num;


    private static List<Question> QUESTIONS;

    static {
        List<String> ls = new ArrayList<>(Arrays.asList("What are 4 brands of clothing?",
                "What are 5 things that might wake you up other than your alarm?",
                "What are 4 instruments you would not want to hear a beginner play?",
                "What are 5 big mammals?",
                "What are 4 types of hot drink?",
                "What are 4 excellent cruise locations?",
                "What are 5 things that live in water besides fish?",
                "What are 4 things you are not allowed to mail?",
                "What are 3 snack foods?",
                "Which 2 people in the room are most likely to watch dramas?",
                "What are 3 woman's names that beginning with the \"N\"?",
                "What are 3 jobs you don't want if you can't stand the sight of blood?",
                "What are 4 things that come to mind when you think of the 1990s?",
                "What are 3 things that come to mind when you think of the 2000s?",
                "What are 3 car brands that would be good names for a pet?",
                "What are 4 ways to fill in the blank __________ ring?",
                "Who are 3 presidents?",
                "What are 5 birds?",
                "What are 3 healthy foods that taste good?",
                "What are 5 things that come in boxes?",
                "What are 5 little fish?",
                "What are 5 cities in Europe?",
                "What are 4 high-calorie foods?",
                "What are 4 things that might keep you awake?",
                "What are 4 Christmas decorations?",
                "Which 3 sidekicks Disney characters are the most helpful?",
                "What are 3 animals one kid might scare another kid with?",
                "What are 5 things every emergency kit should have?",
                "What are 4 pizza toppings?",
                "What are 5 popular emojis?",
                "Which 3 people in the room would make the best lawyers?",
                "What are 5 things that come to mind when you think of Hawaii?",
                "What are 4 difficult sports?",
                "What are 4 fruits that have seeds?",
                "What are 3 things that you might find in a cluttered office?",
                "What are 5 paper things?",
                "What are 3 names from Star Wars that would also be good names for pets?",
                "Which 2 people in the room are the biggest animal lovers?",
                "Which 3 people in the room would give the best restaurant recommendations?",
                "Who are 5 famous Bills?",
                "What are 4 U.S. cities that would make good names for a child?",
                "What are 4 of Santa's reindeers?",
                "What are 3 ways to fill in the blank? Miss ____________",
                "What are 3 volcanoes?",
                "Which 3 animals that begin with the letter \"G\"?",
                "Which 2 people in the room would make the best superheroes?",
                "Which 2 people in the room would make the best supervillains?",
                "What are 5 things that come to mind when you think of national parks?",
                "Who are 4 famous kpop stars?",
                "What are 3 ways to fill in the blank? _______ court",
                "What are 3 most interesting subjects in school?",
                "What are the 5 most common letters?",
                "What are 3 types of lunchmeats?",
                "What are 4 things a burglar might steal from a museum?",
                "What are 3 ways to fill in the blank? top _______",
                "What are 3 popular board games besides Monopoly?",
                "Which 2 people in the room would make the best leaders in a zombie apocalypse?",
                "What are 5 things that come to mind when you think of space?",
                "What are 3 musical instruments you need good lungs for?",
                "What are 5 reasons a baby might be upset?",
                "What are 3 things glow?",
                "What are 3 ways to fill in the blank? The last ____",
                "What are 3 mountains in the U.S.?",
                "What are 5 words that begin with the letter \"U\"?",
                "What are 4 things found in a pyramid?",
                "What are the 5 most common words?",
                "What are 3 types of medications?",
                "What are 3 kinds of pies?",
                "What are 3 things that kids sell door-to-door?",
                "What are 4 things a ninja might carry?",
                "What are 4 reasons you might send food back for at a restaurant?",
                "What are 3 reasonable excuses for not showing up to your best friend's birthday party?",
                "What are 3 toppings that would not go well on a pizza?",
                "What are 3 movies with the word \"time\" in the title?",
                "Which 2 people in the room would most make the best parents?",
                "What are 3 movies with a terrible ending?",
                "Which 2 people in the room would make the best rulers of a small country?",
                "What are 5 things you that come to mind when you think of chocolate?",
                "What are 3 ways to fill in the blank? King __________",
                "What are 3 things that can be broken?",
                "What are 4 dangerous hobbies?",
                "What are 3 countries known for ancient ruins?",
                "What are 3 ways to fill in the blank? Fire__________",
                "What are 3 things people slice?",
                "What are 4 things packaged in glass?",
                "What are 4 great cities to visit?",
                "What are 4 rooms in a house?",
                "What are 4 foods you might feed a baby?",
                "What are 5 things you would find in the garage?",
                "What are 3 popular Halloween costumes?",
                "What are 3 countries with lots of rich people?",
                "What are 4 least watched sports in the Olympics?",
                "What are 5 shiny things?",
                "What are 4 synonyms for \"pretty\"?",
                "What are 3 of the most common slang words used in the English language today?",
                "What are 5 things found in a junk drawer?",
                "What are 4 elements that appear on the periodic table?",
                "What are 4 football teams?",
                "What are 5 scary things?",
                "What are 4 fruits that would make good names for a child?",
                "What are 5 red things?",
                "What are 4 words that begin with \"str\"?",
                "What are 5 cute things?",
                "What are 3 lakes?",
                "What are 4 major fast food restaurants?",
                "What are 3 words an official might say at a tennis match?",
                "What are 3 Pokémon?",
                "What are 4 finger foods?",
                "What are 5 places where you might wait in a long line?",
                "What are 5 words that end in the letter Z?",
                "What are 4 ways to fill in the blank? ________ card",
                "What are 3 jobs that require a suit?",
                "What are 4 vegetables that would make good names for a child?",
                "What are 3 means of communication?",
                "What are 4 animals that you can ride?",
                "What are 4 European languages?",
                "What are 5 things that might break a window?",
                "What are 3 things with wires?",
                "What are 3 of the most intelligent dog breeds?",
                "What are 3 foods that are the noisy to eat?",
                "What are 4 things that come to mind when you think of Scotland?",
                "What are 3 things many people are allergic to?",
                "What are 3 food dishes that difficult to make?",
                "What are 3 kinds of snakes?",
                "What are 3 ways to fill in the blank? _______ boat",
                "Which 2 people in the room would not enjoy being a police officer?",
                "What are 5 things that come to mind when you think of night?",
                "What are 3 movies featuring a high school?",
                "What are 4 ways to fill in the blank? sleep ________",
                "What are 3 major retail chains?",
                "What are 3 items of clothing you wear in the winter?",
                "What are 3 things to be careful of when swimming in the ocean?",
                "Who are 3 overrated actors?",
                "What are 4 movies with robots?",
                "What are 3 safe things?",
                "What are 4 things at a dentist's office?",
                "What are 3 sports that don't use a ball?",
                "Which 2 people in the room at most likely to use the phrase \"flex\"?",
                "What are the 5 most forgettable U.S. states?",
                "What are 3 types of flooring?",
                "What are 4 reasons a flight might be delayed?",
                "What are 3 ways to fill in the blank? Cool as _________",
                "What are the 4 most popular ice cream flavors besides chocolate and vanilla?",
                "What are 3 common street names?",
                "What are 3 types of doctors?",
                "What are 3 types of clocks?",
                "What are 5 things that someone might dig up?",
                "What are 4 things that someone might find that the beach?",
                "What are 3 words that are spelled the same backwards and forwards?",
                "What are 3 types of dancing?",
                "What are 3 kinds of dip for chips?",
                "What are 4 symbols you might find on a map?",
                "What are 4 strange jobs?",
                "What are 4 things that people collect?",
                "Which 2 people in the room are the best listeners?",
                "What are 3 things your parents give you?",
                "What are 3 steel things?",
                "What are 4 things you see a lot of in New York City?",
                "What are 4 genres of music?",
                "What are the 4 foods better served cold than hot?",
                "Who are 4 Disney princesses?",
                "What are 3 things you use to clean the house?",
                "Who are 5 famous Johns?",
                "What are 3 sitcoms with 4 or more characters?",
                "What are 5 condiments?",
                "What are 4 movies with planes?",
                "What are 5 things that come to mind when you think of cooking?",
                "What are 3 things found in a supermarket store?",
                "What are 3 things you shouldn't eat while on a diet?",
                "Who are 3 people to turn to for advice?",
                "What are 3 carnival games?",
                "What are 3 wild animals you would like to have as a tamed pet?",
                "What are 4 things that might be passed from previous generations?",
                "What are 3 ways to fill in the blank? doctor _________",
                "What are 4 things a pirate might carry?",
                "What are the 4 most delicious desserts?",
                "What are 3 types of tea?",
                "What are 3 things that burn?",
                "What are 3 sports with crazy fans?",
                "Who is the luckiest person in the room?",
                "What are the 4 big monsters?",
                "What are 3 things people only want to do once?",
                "Who are 3 kings?",
                "What are 4 uses for duct tape?",
                "What are 3 very long movies?",
                "What are 3 things in a swamp?",
                "What are the 3 worst things about living in a city?",
                "Which 2 people in the room have the best poker faces?",
                "What are 3 types of books?",
                "What are 3 outdated modes of transportation?",
                "What are 3 things at a funeral?",
                "What are 3 working dogs?",
                "What are 4 things an astronaut might see?",
                "What are 4 things a wizard might wear?",
                "What are 3 things women do that men don't?",
                "What are 3 things men do that women don't?",
                "What are 5 things you did on the playground in 5th grade?",
                "What are 4 books of the Bible?",
                "Who are 3 people in the Bible, aside from Jesus?",
                "What are 3 fads that have occurred in your lifetime?",
                "What are 4 buildings on Penn's campus?",
                "What are 2 UPenn clubs?",
                "What are 5 famous books/series from your childhood?",
                "What are 3 streets on Penn's campus?",
                "What are 3 ways to fill in the blank? _______ egg",
                "What are the 2 senses you would lose if you had to? (touch, taste, hearing, sight, smell)",
                "What are 3 video games?",
                "What are 2 knock-knock jokes?",
                "What are the top 4 places people want to visit?",
                "What are 4 quarantine activities?",
                "What are 4 breakfast cereals?"));

        QUESTIONS = new ArrayList<>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m;
        for (String s : ls) {
            m = p.matcher(s);
            if (m.find()) {
                QUESTIONS.add(new Question(s, Integer.parseInt(m.group())));
            } else {
                QUESTIONS.add(new Question(s, 1));
            }
        }
    }

    public Question(String q, int num) {
        this.text = q;
        this.num = num;
    }

    public static List<Question> getQuestions() {
        Collections.shuffle(QUESTIONS);
        return QUESTIONS.subList(0, 3);
    }

    public String getText() {
        return text;
    }

    public int getNum() {
        return num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return num == question.num &&
                text.equals(question.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, num);
    }
}
