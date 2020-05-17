package net.jfabricationgames.todo.frame;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.jfabricationgames.todo.commands.button.appendix.NewAppendixCommand;
import net.jfabricationgames.todo.commands.button.appendix.RemoveAppendixCommand;
import net.jfabricationgames.todo.frame.util.DialogUtils;
import net.jfabricationgames.todo.frame.util.GuiUtils;

public class TodoAppendixDialogController implements Initializable {
	
	public static final String DEFAULT_FILE_DIR = TodoTabController.DEFAULT_FILE_DIR + ".appendix/";
	
	@FXML
	private ListView<AppendixFile> listAppendix;
	@FXML
	private Button buttonAdd;
	@FXML
	private Button buttonRemove;
	@FXML
	private Button buttonNormalSize;
	@FXML
	private ToggleButton toggleButtonFitSize;
	@FXML
	private ImageView imageViewAppendix;
	@FXML
	private ScrollPane scrollPaneImage;
	@FXML
	private StackPane stackPaneImage;
	@FXML
	private TextField textFieldAppendixFileName;
	@FXML
	private Slider sliderZoom;
	
	private Stage stage;
	
	private TodoFrameController controller;
	
	private double zoom = 1;
	
	public TodoAppendixDialogController(TodoFrameController controller) {
		this.controller = controller;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createDirectories();
		initializeButtonFunctions();
		initializeAppendixList();
		initializeTextField();
		initializeImagePasting();
		initializeImageDisplaying();
		initializeZoom();
		initializeToggleButtons();
	}
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void show() {
		stage.show();
	}
	public void hide() {
		stage.hide();
	}
	
	public void requestFocus() {
		stage.requestFocus();
	}
	
	public void updateTodo() {
		updateTodo(false);
	}
	public void updateTodo(boolean force) {
		Optional<TodoTabController> tabControllerOptional = controller.getCurrentTabController();
		if (tabControllerOptional.isPresent()) {
			TodoTabController tabController = tabControllerOptional.get();
			if (force || stage.isShowing()) {
				List<File> appendixFiles = getAppendix(tabController.getFile());
				listAppendix.getItems().clear();
				listAppendix.getItems().addAll(appendixFiles.stream().map(AppendixFile::new).collect(Collectors.toList()));
				imageViewAppendix.setImage(null);
				listAppendix.getSelectionModel().select(0);
			}
			
			//update the button on the TodoFrameController
			controller.setAppendixButtonImage(hasAppendixFiles(tabController.getFile()));
		}
	}
	
	public void deleteSelectedAppendix() {
		List<AppendixFile> selected = listAppendix.getSelectionModel().getSelectedItems();
		if (!selected.isEmpty()) {
			DialogUtils.showConfirmationDialog("Delete Selected", "Are you sure you want to delete the selected Appendix?", "",//
					() -> {// on confirm
						listAppendix.getSelectionModel().getSelectedItems().stream().forEach(file -> file.getFile().delete());
						updateTodo();
					}, //
					() -> {// on cancel
					});
		}
	}
	
	/**
	 * Get the current window for showing dialogs
	 */
	public Window getWindow() {
		return listAppendix.getScene().getWindow();
	}
	
	public boolean isFitImageSize() {
		return toggleButtonFitSize.isSelected();
	}
	
	public void setFitImageSize(boolean fit) {
		toggleButtonFitSize.setSelected(fit);
	}
	
	//***********************************************************************************
	//*** private
	//***********************************************************************************
	
	private void createDirectories() {
		File file = new File(DEFAULT_FILE_DIR);
		file.mkdirs();
	}
	
	private void initializeButtonFunctions() {
		NewAppendixCommand newAppendixCommand = new NewAppendixCommand(this);
		buttonAdd.setOnAction(e -> newAppendixCommand.execute());
		RemoveAppendixCommand removeAppendixCommand = new RemoveAppendixCommand(this);
		buttonRemove.setOnAction(e -> removeAppendixCommand.execute());
		buttonNormalSize.setOnAction(e -> {
			sliderZoom.setValue(100);
			setZoom(1);
		});
	}
	
	private void initializeAppendixList() {
		listAppendix.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				if (newValue.isImage()) {
					newValue.loadImageToView();
				}
				textFieldAppendixFileName.setText(newValue.getAppendixFileName());
				updateImageZoom();
			}
			else {
				textFieldAppendixFileName.setText("");
			}
		});
	}
	
	private void initializeTextField() {
		//change file name on enter key
		textFieldAppendixFileName.setOnAction(e -> {
			Optional<AppendixFile> selectedFileOptional = getSelectedFile();
			if (selectedFileOptional.isPresent()) {
				AppendixFile selectedFile = selectedFileOptional.get();
				selectedFile.setFileName(textFieldAppendixFileName.getText());
				updateTodo();
				selectAppendixFile(selectedFile);
			}
		});
	}
	
	private void initializeImagePasting() {
		EventHandler<? super KeyEvent> pasteImage = e -> {
			if (e.getCode() == KeyCode.V && e.isControlDown()) {
				Optional<AppendixFile> created = createAppendixImageFromClipboard();
				updateTodo();
				created.ifPresent(this::selectAppendixFile);
			}
		};
		
		imageViewAppendix.setOnKeyPressed(pasteImage);
		scrollPaneImage.setOnKeyPressed(pasteImage);
		textFieldAppendixFileName.setOnKeyPressed(pasteImage);
		sliderZoom.setOnKeyPressed(pasteImage);
		listAppendix.setOnKeyPressed(e -> {
			//handle paste image
			pasteImage.handle(e);
			//also handle delete key
			if (e.getCode() == KeyCode.DELETE) {
				new RemoveAppendixCommand(this).execute();
			}
		});
	}
	
	private void initializeImageDisplaying() {
		stackPaneImage.minWidthProperty()
				.bind(Bindings.createDoubleBinding(() -> scrollPaneImage.getViewportBounds().getWidth(), scrollPaneImage.viewportBoundsProperty()));
		stackPaneImage.minHeightProperty()
				.bind(Bindings.createDoubleBinding(() -> scrollPaneImage.getViewportBounds().getHeight(), scrollPaneImage.viewportBoundsProperty()));
	}
	
	private void initializeZoom() {
		sliderZoom.setBlockIncrement(10);
		sliderZoom.valueProperty().addListener((observable, oldVal, newVal) -> setZoom(newVal.doubleValue() / 100d));
	}
	
	private Optional<AppendixFile> getSelectedFile() {
		return Optional.ofNullable(listAppendix.getSelectionModel().getSelectedItem());
	}
	
	/**
	 * Get a list of all appendix files that are linked to the current ToDo.
	 * 
	 * @param todoFile
	 *        The file of the currently opened ToDo
	 * 		
	 * @return A list of all appendix files that are linked to the current ToDo.
	 */
	private List<File> getAppendix(File todoFile) {
		if (todoFile == null) {
			return Collections.emptyList();
		}
		List<File> allAppendixFiles = getAllAppendixFiles();
		String todoFileName = todoFile.getName().substring(0, todoFile.getName().lastIndexOf("."));
		return allAppendixFiles.stream().filter(file -> file.getName().startsWith(todoFileName)).collect(Collectors.toList());
	}
	
	private boolean hasAppendixFiles(File todoFile) {
		return !getAppendix(todoFile).isEmpty();
	}
	
	private List<File> getAllAppendixFiles() {
		File dir = new File(DEFAULT_FILE_DIR);
		File[] files = dir.listFiles();
		if (files == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(files);
	}
	
	private void selectAppendixFile(AppendixFile appendixFile) {
		int index = -1;
		for (int i = 0; i < listAppendix.getItems().size(); i++) {
			if (listAppendix.getItems().get(i).getAppendixFileName().equals(appendixFile.getAppendixFileName())) {
				index = i;
			}
		}
		if (index != -1) {
			listAppendix.getSelectionModel().select(index);
		}
	}
	
	private Optional<AppendixFile> createAppendixImageFromClipboard() {
		Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (content != null && content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				BufferedImage img = (BufferedImage) content.getTransferData(DataFlavor.imageFlavor);
				String filename = createNextAppendixFileName(".png");
				if (filename != null) {
					File outfile = new File(filename);
					ImageIO.write(img, "png", outfile);
					
					return Optional.of(new AppendixFile(outfile));
				}
			}
			catch (UnsupportedFlavorException | IOException e) {
				DialogUtils.showExceptionDialog("Pasting Error", "The image could not be pasted", e, false);
			}
		}
		else {
			DialogUtils.showInfoDialog("No image", "There is no image in the clipboard", "The image could not be created");
		}
		return Optional.empty();
	}
	
	private String createNextAppendixFileName(String fileEnding) {
		AppendixFile empty = new AppendixFile();
		String fileName;
		try {
			fileName = DEFAULT_FILE_DIR + empty.getFileNamePrefix() + "appendix" + fileEnding;
		}
		catch (IllegalStateException ise) {
			DialogUtils.showErrorDialog("No TODO File", "The \".todo\" file was not yet created",
					"Please save the Todo before proceeding to add an appendix");
			return null;
		}
		
		File file = new File(fileName);
		//choose a file that doesn't exist yet
		int postfix = 2;
		while (file.exists()) {
			int substringEndIndex = fileName.lastIndexOf(".");
			if (postfix > 2) {
				substringEndIndex -= 2;
			}
			String nextFileName = fileName.substring(0, substringEndIndex) + "_" + postfix + fileEnding;
			postfix++;
			file = new File(nextFileName);
			fileName = nextFileName;
		}
		return file.getAbsolutePath();
	}
	
	private void setZoom(double zoom) {
		this.zoom = zoom;
		toggleButtonFitSize.setSelected(false);
		updateImageZoom();
	}
	
	private void updateImageZoom() {
		if (imageViewAppendix.getImage() != null) {
			if (!toggleButtonFitSize.isSelected()) {
				double width = imageViewAppendix.getImage().getWidth() * zoom;
				double height = imageViewAppendix.getImage().getHeight() * zoom;
				imageViewAppendix.setFitWidth(width);
				imageViewAppendix.setFitWidth(height);
			}
		}
	}
	
	private void initializeToggleButtons() {
		ChangeListener<? super Bounds> changeListener = (observable, oldValue, newValue) -> {
			imageViewAppendix.setFitWidth(newValue.getWidth() * 1.25);
			imageViewAppendix.setFitWidth(newValue.getHeight() * 1.25);
		};
		
		toggleButtonFitSize.setOnAction(e -> {
			if (toggleButtonFitSize.isSelected()) {
				scrollPaneImage.viewportBoundsProperty().addListener(changeListener);
				imageViewAppendix.setFitWidth(scrollPaneImage.getViewportBounds().getWidth() * 1.25);
				imageViewAppendix.setFitWidth(scrollPaneImage.getViewportBounds().getHeight() * 1.25);
			}
			else {
				scrollPaneImage.viewportBoundsProperty().removeListener(changeListener);
			}
		});
	}
	
	//***********************************************************************************
	//*** classes
	//***********************************************************************************
	
	private class AppendixFile {
		
		private File file;
		
		private AppendixFile() {
			this(null);
		}
		public AppendixFile(File file) {
			this.file = file;
		}
		
		public File getFile() {
			return file;
		}
		
		public boolean isImage() {
			return file.getName().endsWith(".png") || file.getName().endsWith(".jpg");
		}
		
		public void loadImageToView() {
			GuiUtils.loadImageToView("file:///" + file.getAbsolutePath(), imageViewAppendix);
		}
		
		public void setFileName(String name) {
			String newName = getFileNamePrefix() + name;
			File currentDir = file.getParentFile();
			File newFile = new File(currentDir.getAbsolutePath() + "/" + newName);
			if (!newFile.exists()) {
				file.renameTo(newFile);
				file = newFile;
			}
			else {
				throw new IllegalStateException("A file with this name already exists");
			}
		}
		
		public String getAppendixFileName() {
			String filePrefix = getFileNamePrefix();
			return file.getName().substring(filePrefix.length());
		}
		
		public String getFileNamePrefix() throws IllegalStateException {
			String prefix = null;
			Optional<TodoTabController> tabControllerOptional = controller.getCurrentTabController();
			if (tabControllerOptional.isPresent()) {
				if (tabControllerOptional.get().getFile() != null) {
					String todoFileName = tabControllerOptional.get().getFile().getName();
					prefix = todoFileName.substring(0, todoFileName.lastIndexOf(".")) + "__";
				}
			}
			
			if (prefix == null) {
				throw new IllegalStateException("The prefix name could not be created because there is no todo file present.");
			}
			
			return prefix;
		}
		
		@Override
		public String toString() {
			return getAppendixFileName();
		}
	}
}
