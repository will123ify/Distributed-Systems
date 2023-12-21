package google_file_sync;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.HashMap;




/* class to demonstrate use of Drive files list API */
public class DriveQuickstart {
  /**
   * Application name.
   */
  private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  /**
   * Directory to store authorization tokens for this application.
   */
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive");

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  
  
   private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    //returns an authorized Credential object.
    return credential;
  }


  private static Map<String, BasicFileAttributes> readLocalFiles(String directoryPath) throws IOException {
    Map<String, BasicFileAttributes> localFiles = new HashMap<>();
    Files.walk(Paths.get(directoryPath))
         .filter(Files::isRegularFile)
         .forEach(path -> {
             try {
                 BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                 localFiles.put(path.getFileName().toString(), attrs);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         });
    return localFiles;
  }


  private static Map<String, File> processDriveFiles(List<File> driveFiles) {
    Map<String, File> processedFiles = new HashMap<>();
    for (File file : driveFiles) {
        processedFiles.put(file.getName(), file);
    }
    return processedFiles;
  }


  private static void uploadFileToDrive(Drive service, String folderId, Path localFilePath, String fileName) throws IOException {
    // Get the MIME type of the file
    String mimeType = getMimeType(localFilePath.toFile());

    // Create metadata for the file
    File fileMetadata = new File();
    fileMetadata.setName(fileName);
    // Set the parent folder ID
    if (folderId != null && !folderId.isEmpty()) {
        fileMetadata.setParents(Collections.singletonList(folderId));
    }

    // Prepare the file content
    java.io.File fileContent = localFilePath.toFile();
    FileContent mediaContent = new FileContent(mimeType, fileContent);

    // Upload the file to Google Drive
    File file = service.files().create(fileMetadata, mediaContent)
            .setFields("id, parents")
            .execute();
    System.out.println("File ID: " + file.getId());
  }


  private static void updateFileOnDrive(Drive service, String fileId, Path localFilePath, String localFileName) throws IOException {
    // Create a new File object with the updated metadata using fully qualified name
    com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
    fileMetadata.setName(localFileName);

    // Set the modified time of the file to the current time
    DateTime now = new DateTime(System.currentTimeMillis());
    fileMetadata.setModifiedTime(now);

    // Create a new media content with the updated file content
    FileContent mediaContent = new FileContent(getMimeType(localFilePath.toFile()), localFilePath.toFile());

    // Update the file on Google Drive
    service.files().update(fileId, fileMetadata, mediaContent).execute();
  }


private static String getMimeType(java.io.File file) throws IOException {
  return Files.probeContentType(file.toPath());
}




///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public static void main(String... args) throws IOException, GeneralSecurityException {
    // Build a new authorized API client service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

    // Searching on the Drive for a specified folder
    String folderId = null;
    FileList folderResult = service.files().list()
        .setQ("mimeType='application/vnd.google-apps.folder' and name='Labb 6'")
        .setSpaces("drive")
        .setFields("files(id, name)")
        .execute();

    // Checks to find the folder
    for (File file : folderResult.getFiles()) {
        System.out.println("Found folder: " + file.getName() + " (" + file.getId() + ")");
        folderId = file.getId();
    }

    if (folderId == null) {
        System.out.println("Folder 'Labb 6' not found on Google Drive.");
        return;
    }

    // List the files locally
    FileList fileResult = service.files().list()
        .setQ("'" + folderId + "' in parents")
        .setSpaces("drive")
        .setFields("nextPageToken, files(id, name, modifiedTime)")
        .execute();

    List<File> filesInFolder = fileResult.getFiles();
    
    if (filesInFolder.isEmpty()) {
        System.out.println("No files found in the 'Labb 6' folder.");
    } else {
        for (File file : filesInFolder) {
            System.out.println("File in 'Labb 6' folder: " + file.getName() + " (" + file.getId() + ")");
        }
    }

    // Read local files (Specified folder)
    String localDirectoryPath = "C:\\Users\\franj\\Desktop\\Labb 6";
    Map<String, BasicFileAttributes> localFiles = readLocalFiles(localDirectoryPath);

    // Process Drive files into a Map for easy lookup.
    Map<String, File> processedDriveFiles = new HashMap<>();
    for (File file : filesInFolder) {
        processedDriveFiles.put(file.getName(), file);
    }

    // Compare files
    for (Map.Entry<String, BasicFileAttributes> localFileEntry : localFiles.entrySet()) {
      String localFileName = localFileEntry.getKey();
      BasicFileAttributes localFileAttrs = localFileEntry.getValue();
      FileTime localFileModifiedTime = localFileAttrs.lastModifiedTime();

      File driveFile = processedDriveFiles.get(localFileName);

      if (driveFile == null) {
        // The file does not exist on Drive, upload it.
        Path localFilePath = Paths.get(localDirectoryPath, localFileName);
        uploadFileToDrive(service, folderId, localFilePath, localFileName); // Saves the new files from the read directory on Drive
      } else {
        // The file exists on Drive, compare last modified dates
        DateTime driveFileModifiedTime = driveFile.getModifiedTime();
        FileTime driveFileTime = FileTime.fromMillis(driveFileModifiedTime.getValue());
        
        if (driveFileTime.compareTo(localFileModifiedTime) < 0) {
          // The local file is newer, update the file on Drive.
          Path localFilePath = Paths.get(localDirectoryPath, localFileName);
          updateFileOnDrive(service, driveFile.getId(), localFilePath, localFileName);
        }
      }
    }





  }
}






/*     // Retrieve the list of files from Google Drive.
    FileList result = service.files().list()
        .setPageSize(10)
        .setFields("nextPageToken, files(id, name, modifiedTime)")
        .execute();

    List<File> driveFiles = result.getFiles();  // Initialize the driveFiles variable here

    if (driveFiles == null || driveFiles.isEmpty()) {
      System.out.println("No files found.");
      return;
    }

    // Read local files
    String localDirectoryPath = "C:\\Users\\franj\\Desktop\\Labb 6";
    Map<String, BasicFileAttributes> localFiles = readLocalFiles(localDirectoryPath);

    // Process Drive files
    Map<String, File> processedDriveFiles = processDriveFiles(driveFiles);

    // Compare files
    for (Map.Entry<String, BasicFileAttributes> localFileEntry : localFiles.entrySet()) {
      String localFileName = localFileEntry.getKey();
      BasicFileAttributes localFileAttrs = localFileEntry.getValue();
      FileTime localFileModifiedTime = localFileAttrs.lastModifiedTime();

      File driveFile = processedDriveFiles.get(localFileName);

      if (driveFile == null) {
        // The file does not exist on Google Drive, upload it.
        Path localFilePath = Paths.get(localDirectoryPath, localFileName);
        uploadFileToDrive(service, localFilePath, localFileName);
      } else {
        // The file exists on Google Drive, compare last modified dates
        DateTime driveFileModifiedTime = driveFile.getModifiedTime();
        FileTime driveFileTime = FileTime.fromMillis(driveFileModifiedTime.getValue());
        
        if (driveFileTime.compareTo(localFileModifiedTime) < 0) {
          // The local file is newer, update the file on Google Drive.
          Path localFilePath = Paths.get(localDirectoryPath, localFileName);
          updateFileOnDrive(service, driveFile.getId(), localFilePath, localFileName);
        }
      }
    } */