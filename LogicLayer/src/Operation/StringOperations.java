package Operation;

public enum StringOperations implements Operation{
    CONCAT{
        @Override
        public String eval(String... args) {
            if(args.length == 2){
                return String.valueOf(args[0].concat(args[1]));
            }

            throw new NumberFormatException("Concat operation requires two arguments");
        }
    },
    SUB{
        @Override
        public String eval(String... args) {
            if(args.length == 3){
                int startIndex = Integer.parseInt(args[1]);
                int endIndex = Integer.parseInt(args[2]);

                return args[0].substring(startIndex, endIndex);
            }

            throw new NumberFormatException("Sub operation requires three arguments");
        }
    }
}
