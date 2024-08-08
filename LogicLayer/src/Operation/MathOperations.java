package Operation;

public enum MathOperations implements Operation  {
    PLUS{
        @Override
        public String  eval(String... args) {
            if(args.length == 2){
                double num1 = Double.parseDouble(args[0]);
                double num2 = Double.parseDouble(args[1]);

                return String.valueOf(num1 + num2);
            }

            throw new NumberFormatException("Plus operation requires two arguments");
        }
    },
    MINUS{
        @Override
        public String eval(String... args) {
            if(args.length == 2){
                double num1 = Double.parseDouble(args[0]);
                double num2 = Double.parseDouble(args[1]);

                return String.valueOf(num1 - num2);
            }

            throw new NumberFormatException("Minus operation requires two arguments");
        }
    },
    TIMES{
        @Override
        public String eval(String... args) {
            if(args.length == 2){
                double num1 = Double.parseDouble(args[0]);
                double num2 = Double.parseDouble(args[1]);

                return String.valueOf(num1 * num2);
            }

            throw new NumberFormatException("Times operation requires two arguments");
        }
    },
    DIVIDE{
        @Override
        public String eval(String... args) {
            if(args.length == 2){
                double num1 = Double.parseDouble(args[0]);
                double num2 = Double.parseDouble(args[1]);

                if(num2 == 0){
                    throw new NumberFormatException("NaN");
                }

                return String.valueOf(num1 / num2);
            }

            throw new NumberFormatException("Divide operation requires two arguments");
        }
    },
    MOD{
        @Override
        public String eval(String... args) {
            if(args.length == 2){
                double num1 = Double.parseDouble(args[0]);
                double num2 = Double.parseDouble(args[1]);

                if(num2 == 0){
                    throw new NumberFormatException("NaN");
                }

                return String.valueOf(num1 % num2);
            }

            throw new NumberFormatException("Mod operation requires two arguments");
        }
    },
    POW{
        @Override
        public String eval(String... args) {
            if(args.length == 2){
                double num1 = Double.parseDouble(args[0]);
                double num2 = Double.parseDouble(args[1]);

                return String.valueOf(Math.pow(num1, num2));
            }

            throw new NumberFormatException("Times operation requires two arguments");
        }
    },
    ABS{
        @Override
        public String eval(String... args) {
            if(args.length == 1){
                return String.valueOf(Math.abs(Double.parseDouble(args[0])));
            }

            throw new NumberFormatException("Abs operation requires one argument");
        };
    }
};




