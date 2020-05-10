//package smartshare.administrationservice.dto.response.usertree;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class Test {
//
//    private static UserFolderComponent identifyNameOfFolder(String currentFolder, UserFolderComponent previousFolder){
//
//        List<String> previousFolderSplits = Arrays.asList( previousFolder.getCompleteName().split( "/" ) );
//        List<String> currentFolderSplits =  Arrays.asList(currentFolder.split( "/" ));
//
//
//
//        // scenario 1
//
//        if(!previousFolderSplits.get(previousFolderSplits.size()-1  ).equals( currentFolderSplits.get(currentFolderSplits.size()-1  ))){
//
//            previousFolder =  (UserFolderComponent) previousFolder.add(new UserFolderComponent( currentFolderSplits.get(currentFolderSplits.size()-1  ),currentFolder ));
//
//        }
//
//
////
////        for(int i=0; i< previousFolderSplits.size() ; i++){
////
////
////
////
////            if(previousFolderSplits.get( i ).equals( currentFolderSplits.get( i ) )){
////                currentFolderSplits.remove( i );
////
////            }else{
////
////                previousFolder = (UserFolderComponent) previousFolder.add(new UserFolderComponent( currentFolderSplits.toString(),"currentFolder",previousFolder ));
////            }
////        }
//
//
//        return previousFolder;
//    }
//
//    public static void main ( String[] args) {
//
//        List<String> folders = new ArrayList<>(  );
//        folders.add( "play/" );
//        folders.add( "play/scala/" );
//        folders.add( "play/scala/random/" );
//        folders.add( "play/scala/sample/" );
//        folders.add( "play/scala/sample/sample4/" );
//        folders.add( "play/scala/sample/sample4/sample5/" );
//        folders.add( "play/scala/sample/sample4/sample5/aramco/" );
//
//        UserFolderComponent root = new UserFolderComponent( "bucketName","aramco/" );
//
//
////        for (String currentFolderName : folders) {
////            root = identifyNameOfFolder( currentFolderName, root );
////        }
//
//
//
//
//
//        //            bucketObject------------>basraja@gmail.com/
////            bucketObject------------>motivation.pdf
////
////            bucketObject------------>play/
////            bucketObject------------>play/Play Framework Essentials.pdf
////            bucketObject------------>play/Redis in Action.pdf
////            bucketObject------------>play/main-qimg-472b917299d6f7ce9797c89ff864b0aa.png
////            bucketObject------------>play/motivation.pdf
////            bucketObject------------>play/programming_in_scala_2nd.pdf
////            bucketObject------------>play/scala/
////            bucketObject------------>play/scala/programming_in_scala_2nd.pdf
////            bucketObject------------>play/scala/random/
////            bucketObject------------>play/scala/sample/
////            bucketObject------------>play/scala/sample/motivation.pdf
////            bucketObject------------>play/scala/sample/sample4/
////            bucketObject------------>play/scala/sample/sample4/sample5/
////            bucketObject------------>play/scala/sample/sample4/sample5/aramco/
//
//    }
//
//
//
//}
