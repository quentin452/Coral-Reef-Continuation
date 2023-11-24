// TODO: re-write (?)
// TODO: profile
// this will definitely be re-written - we can use native types for increased speed :)

package nandonalt.mods.nandocore;

import nandonalt.mods.coralmod.Util;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ModSettings {

	/**
	 * Configuration
	 */
	private final Configuration config;

	/**
	 * Setting map
	 */
	private final Map<String, List<LocalProperty>> modSettings = new LinkedHashMap<String, List<LocalProperty>>();

	public ModSettings(Configuration config) {
		this.config = config;
	}

	/**
	 * Register a list of settings
	 */
	public void register(String key, List<LocalProperty> list) {
		modSettings.put(key, list);
	}

	/**
	 * Get list of setting names
	 */
	public List<String> getNames(String key) {
		final List<String> names = new LinkedList<String>();
		final List<LocalProperty> list = modSettings.get(key);
		if(list != null)
			for(LocalProperty prop : list) names.add(prop.getName());
		return names;
	}

	/**
	 * Gets specified setting
	 */
	private LocalProperty getSetting(String key, String s) {
		final List<LocalProperty> list = modSettings.get(key);
		for(LocalProperty prop : list) {
			if(prop.getName().equals(s)) return prop;
		}
		return null;
	}

	/**
	 * Attempts to toggle specified setting
	 */
	public boolean toggle(String key, String s) {
		if(!getNames(key).contains(s)) {
			Util.log("Unknown setting " + s, true);
			return false;
		}

		final LocalProperty prop = getSetting(key, s);
		return (prop == null) ? false : prop.toggle();
	}

	/**
	 * Attempts to return value of specified setting
	 */
	public String getValue(String key, String s) {
		if(!getNames(key).contains(s)) {
			Util.log("Unknown field " + s, true);
			return "";
		}

		final LocalProperty prop = getSetting(key, s);
		return (prop == null) ? "" : prop.getString();
	}

	/**
	 * Set value of specified setting
	 */
	/* UNUSED
    public boolean setValue(String key, String s, String value) {
        if(!getNames(key).contains(s)) {
            System.err.println("CoralMod: Unknown field " + s);
            return false;
        }

        final LocalProperty prop = getSetting(key, s);
        if(prop == null) {
            return false;
        } else {
            prop.set(value);
            return true;
        }
    }
	 */

	/**
	 * Gets boolean value for specified setting
	 */
	public boolean getBooleanValue(String key, String s) {
		final LocalProperty prop = getSetting(key, s);
		return (prop == null) ? false : prop.getBoolean(false);
	}

	/**
	 * Gets integer value for specified setting
	 */
	public int getIntValue(String key, String s) {
		final LocalProperty prop = getSetting(key, s);
		return (prop == null) ? 0 : prop.getInt();
	}

	/**
	 * Attempts to load settings, defaults are used if first time
	 */
	public void loadSettings() {
		for(String key : modSettings.keySet()) {
			final List<LocalProperty> list = modSettings.get(key);

			if(list == null) continue;

			for(LocalProperty prop : list) {
				final Property p = config.get(key, prop.getName(), prop.getDefaultValue(), null, prop.getType());
				prop.set(p.getString());
			}
		}

		updateSettings();
	}

	/**
	 * Saves settings
	 */
	public void updateSettings() {
		for(String key : modSettings.keySet()) {
			final List<LocalProperty> list = modSettings.get(key);

			if(list == null) continue;

			for(LocalProperty prop : list) {
				Util.logDebug(key + ": [" + prop.getName() + " = " + prop.getString() + "]");
				final Property p = config.get(key, prop.getName(), prop.getDefaultValue(), null, prop.getType());
				p.set(prop.getString());
			}
		}

		config.save();
		Util.log("Saved settings");
	}

	public static abstract class LocalProperty extends Property {
		LocalProperty(String name, String value, Property.Type type) {
			super(name, value, type);
		}

		public abstract boolean toggle();
		public abstract String getDefaultValue();
	}

	public static final class IntProperty extends LocalProperty {
		private final int defaultVal;
		private final int minVal;
		private final int maxVal;

		public IntProperty(String name, int defaultVal, int minVal, int maxVal) {
			super(name, Integer.toString(defaultVal), Property.Type.INTEGER);
			this.defaultVal = defaultVal;
			this.minVal = minVal;
			this.maxVal = maxVal;
		}

		@Override
		public boolean toggle() {
			if(maxVal < 1 || minVal != 0) return false;
			int val = getInt(0);
			val++;
			set(val % (maxVal + 1));
			return true;
		}

		@Override
		public String getDefaultValue() {
			return Integer.toString(defaultVal);
		}

		@Override
		public void set(String value) {
			try {
				int i = Integer.parseInt(value);
				if(i < minVal || i > maxVal) i = defaultVal;
				super.set(Integer.toString(i));
			} catch (NumberFormatException nfe) {
				super.set(value);
			}
		}
	}

	public static final class BooleanProperty extends LocalProperty {
		private final boolean defaultVal;

		public BooleanProperty(String name, boolean defaultVal) {
			super(name, Boolean.toString(defaultVal), Property.Type.BOOLEAN);
			this.defaultVal = defaultVal;
		}

		@Override
		public boolean toggle() {
			final boolean bool = Boolean.parseBoolean(getString());
			set(!bool);
			return true;
		}

		@Override
		public String getDefaultValue() {
			return Boolean.toString(defaultVal);
		}
	}

	public static final class StringProperty extends LocalProperty {
		private final String defaultVal;

		public StringProperty(String name, String defaultVal) {
			super(name, defaultVal, Property.Type.STRING);
			this.defaultVal = defaultVal;
		}

		@Override
		public boolean toggle() {
			return false;
		}

		@Override
		public String getDefaultValue() {
			return defaultVal;
		}
	}

}