package Operation;

public enum SystemOperations implements Operation{
    REF{
        @Override
        public String eval(String... args) {
            if(args.length == 1){
                //Unfinished
            }

            throw new NumberFormatException("Ref operation requires one argument");
        }
    }
}
