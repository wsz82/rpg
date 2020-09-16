set curpath=%cd%
java --module-path "%curpath%\javafx\lib";"%curpath%\model";"%curpath%" --add-modules=javafx.controls,io.wsz.editor,io.wsz.model -jar editor.jar > editor_log.log 2>&1