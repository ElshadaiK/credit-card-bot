package creditcardbot;

/**
 * Represents a credit card with a name, due date, and closing date.
 */
public class CreditCard {
        private final String name;
        private int dueDate = 15; // Default due date
        private int closingDate = 13; // Default closing date

        public CreditCard(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getDueDate() {
            return dueDate;
        }

        public int getClosingDate() {
            return closingDate;
        }

        public void setDueDate(int dueDate) {
            this.dueDate = dueDate;
        }

        public void setClosingDate(int closingDate) {
            this.closingDate = closingDate;
        }
    }
