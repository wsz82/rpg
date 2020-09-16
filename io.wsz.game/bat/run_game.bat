set curpath=%cd%
java --module-path "%curpath%\javafx\lib";"%curpath%\model";"%curpath%" --add-modules=javafx.controls,io.wsz.game,io.wsz.model -jar game.jar > game_log.log 2>&1