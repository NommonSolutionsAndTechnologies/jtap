package controller;

import java.io.File;
import java.util.Collections;
import java.util.List;
import config.Config;
import controller.modules.DefaultModule;


public class Controller implements ControllerI {
	
	private static com.google.inject.Injector injector;
	
	private static List<AbstractModule> modules = Collections.singletonList(new DefaultModule());
	
	private  static AbstractModule overrides = AbstractModule.emptyModule();
	
	private final Config config;

	public Controller(Config config) {
		this.config = config;
	}
	
	public final void run() {
		injector = Injector.createInjector(this.config,AbstractModule.override(Collections.singleton(new AbstractModule() {
			@Override
			public void install() {
				for (AbstractModule module : modules) {
					install(module);
				}
			}
		}), overrides));
	}
	
	public static com.google.inject.Injector getInjector(){
		return injector;
	}
	
	public static void emptyTempDirectory() {
		File directory = new File(getInjector().getInstance(Config.class).getGeneralConfig().getTempDirectory());
		File[] files = directory.listFiles();    
		for(File file : files) {
		  file.delete();
		}
	}
}
