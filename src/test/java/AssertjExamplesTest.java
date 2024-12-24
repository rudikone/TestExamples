import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.assertj.core.api.Assertions.not;
import static org.assertj.core.api.Assertions.notIn;

/**
 * AssertJ is a Java library that provides a rich set of assertions
 * and truly helpful error messages and improves test code readability.
 * <p>
 * <a href="https://assertj.github.io/doc/">Library documentation</a>
 * <p>
 * <a href="https://github.com/assertj/assertj-examples/tree/main/assertions-examples/src/test/java/org/assertj/examples">More examples</a>
 */
public class AssertjExamplesTest {
    @Nested
    class BasicAssertions {
        @Test
        void a_few_simple_assertions() {
            assertThat("The Lord of the Rings").isNotNull()
                .startsWith("The")
                .contains("Lord")
                .endsWith("Rings");
        }

        @Test
        void dateExample() {
            Instant now = Clock.systemUTC().instant();
            Instant yesterday = now.minus(Duration.ofDays(1));
            assertThat(now).isAfter(yesterday);
        }

        @Test
        void atomicExample() {
            AtomicInteger atomic = new AtomicInteger(0);
            atomic.set(8);
            assertThat(atomic).hasValueBetween(5, 10);
        }

        @Test
        void arrayExample() {
            int[] array = new int[] {5};
            assertThat(array).containsAnyOf(1, 3, 5);
        }

        @Test
        void streamExample() {
            IntStream stream = IntStream.iterate(2, i -> i * 2).limit(100);
            assertThat(stream).allMatch(number -> number % 2 == 0);
        }

        @Test
        void mapExample() {
            Map<String, Integer> map = Map.of("1", 1, "3", 3);
            assertThat(map).hasKeySatisfying(new Condition<>() {
                @Override
                public boolean matches(String value) {
                    return Integer.parseInt(value) % 2 == 1;
                }
            });
        }
    }

    @Nested
    class ObjectAssertions {
        /**
         * Note: it's best to use Instancio (see {@link InstancioExamplesTest}) for random data rather than hard-coding
         */
        TolkienCharacter aragorn = new TolkienCharacter("Aragorn", 33, Race.MAN);
        TolkienCharacter boromir = new TolkienCharacter("Boromir", 33, Race.MAN);

        TolkienCharacter legolas = new TolkienCharacter("Legolas", 33, Race.ELF);
        TolkienCharacter gandalf = new TolkienCharacter("Gandalf", 33, Race.MAIA);
        TolkienCharacter sauron = new TolkienCharacter("Sauron", 33, Race.MAIA);
        TolkienCharacter gimli = new TolkienCharacter("Gimli", 33, Race.DRAWF);

        TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, Race.HOBBIT);
        TolkienCharacter sam = new TolkienCharacter("Sam", 33, Race.HOBBIT);
        TolkienCharacter pippin = new TolkienCharacter("Pippin", 33, Race.HOBBIT);
        TolkienCharacter merry = new TolkienCharacter("Merry", 33, Race.HOBBIT);

        TolkienCharacter[] fellowshipOfTheRing =
            new TolkienCharacter[] {aragorn, frodo, legolas, boromir, sam, pippin, merry, gandalf, gimli};

        @Test
        public void objectEqualityAssertions() {
            assertThat(frodo).isNotEqualTo(sam);
            assertThat(frodo).usingComparator(comparing(TolkienCharacter::race)).isEqualTo(sam);

            // standard comparison : the fellowshipOfTheRing includes Gandalf but not Sauron (believe me) ...
            assertThat(fellowshipOfTheRing).contains(gandalf).doesNotContain(sauron);
            // ... but if we compare race only, Sauron is in fellowshipOfTheRing (he's a Maia like Gandalf)
            assertThat(fellowshipOfTheRing)
                .usingElementComparator(comparing(TolkienCharacter::race))
                .contains(sauron);

            // Fail as equals compares object references
            TolkienCharacter frodoCopy = new TolkienCharacter("Frodo", 33, Race.HOBBIT);
            assertThat(frodo).isEqualTo(frodoCopy);
            // frodo and frodoClone are equal when doing a field by field comparison.
            assertThat(frodo).usingRecursiveComparison().isEqualTo(frodoCopy);

            // frodo and sam both are hobbits, so they are equal when comparing only race
            assertThat(frodo).usingRecursiveComparison().comparingOnlyFields("race").isEqualTo(sam); // OK
            // they are also equals when comparing only race name (nested field).
            assertThat(frodo).usingRecursiveComparison().comparingOnlyFields("race.name").isEqualTo(sam); // OK
        }

        @Test
        public void listAssertions() {
            List<TolkienCharacter> hobbits = List.of(frodo, sam, pippin);

            // all elements must satisfy the given assertions
            assertThat(hobbits).allSatisfy(character -> {
                assertThat(character.race()).isEqualTo(Race.HOBBIT);
                assertThat(character.name()).isNotEqualTo("Sauron");
            });

            // at least one element must satisfy the given assertions
            assertThat(hobbits).anySatisfy(character -> {
                assertThat(character.race()).isEqualTo(Race.HOBBIT);
                assertThat(character.name()).isEqualTo("Sam");
            });

            // no element must satisfy the given assertions
            assertThat(hobbits).noneSatisfy(character -> assertThat(character.race()).isEqualTo(Race.ELF));
        }

        @Test
        public void matchExample() {
            List<TolkienCharacter> hobbits = List.of(frodo, sam, pippin);

            assertThat(hobbits).allMatch(character -> character.race() == Race.HOBBIT, "hobbits")
                .anyMatch(character -> character.name().contains("pp"))
                .noneMatch(character -> character.race() == Race.ORC);
        }

        @Test
        public void filterExample() {
            // filters use introspection to get property/field values
            assertThat(fellowshipOfTheRing).filteredOn("race", Race.HOBBIT)
                .containsOnly(sam, frodo, pippin, merry);

            // nested properties are supported
            assertThat(fellowshipOfTheRing).filteredOn("race.name", "Man")
                .containsOnly(aragorn, boromir);

            // you can apply different comparison
            assertThat(fellowshipOfTheRing).filteredOn("race", notIn(Race.HOBBIT, Race.MAN))
                .containsOnly(gandalf, gimli, legolas);

            assertThat(fellowshipOfTheRing).filteredOn("race", in(Race.MAIA, Race.MAN))
                .containsOnly(gandalf, boromir, aragorn);

            assertThat(fellowshipOfTheRing).filteredOn("race", not(Race.HOBBIT))
                .containsOnly(gandalf, boromir, aragorn, gimli, legolas);

            // you can chain multiple filter criteria
            assertThat(fellowshipOfTheRing).filteredOn("race", Race.MAN)
                .filteredOn("name", not("Boromir"))
                .containsOnly(aragorn);
        }
    }

    @Nested
    class ExceptionAssertions {
        @Test
        public void example() {
            Throwable throwable = new IllegalArgumentException("wrong amount 123");

            assertThat(throwable).hasMessage("wrong amount 123")
                .hasMessage("%s amount %d", "wrong", 123)
                // check start
                .hasMessageStartingWith("wrong")
                .hasMessageStartingWith("%s a", "wrong")
                // check content
                .hasMessageContaining("wrong amount")
                .hasMessageContaining("wrong %s", "amount")
                .hasMessageContainingAll("wrong", "amount")
                // check end
                .hasMessageEndingWith("123")
                .hasMessageEndingWith("amount %s", "123")
                // check with regex
                .hasMessageMatching("wrong amount .*")
                // check does not contain
                .hasMessageNotContaining("right")
                .hasMessageNotContainingAny("right", "price");
        }
    }

    public enum Race {
        HOBBIT("Hobbit"), MAN("Man"), MAIA("Maia"), DRAWF("Dwarf"), ELF("Elf"), ORC("Orc");
        private final String name;

        Race(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public record TolkienCharacter(String name, int age, Race race) {
    }
}
