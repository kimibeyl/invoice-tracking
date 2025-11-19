import Lara from "@primeuix/themes/lara";
import { definePreset } from "@primeuix/themes";

export const MyPreset = definePreset(Lara, {
  semantic: {
    primary: {
      50: "#f3f3f3",
      100: "#3A3A3A",
      200: "#f3f3f3",
      300: "#E51718",
      400: "#4111A4",
      500: "#2F70EF",
      600: "#418fde",
    },
    colorScheme: {
      light: {
        surface: {
          100: "{primary.50}",
          800: "#000",
        },
        primary: {
          color: "{primary.400}",
          inverseColor: "#ffffff",
          hoverColor: "{zinc.900}",
          activeColor: "{zinc.800}",
        },
        highlight: {
          background: "{primary.300}",
          focusBackground: "{primary.300}",
          color: "#ffffff",
          focusColor: "#ffffff",
        },
      },
    },
  },
  components: {
    button: {
      colorScheme: {
        light: {
          text: {
            primary: {
              hoverBackground: "{primary.50}",
            },
          },
        },
      },
    },
    inputtext: {
      colorScheme: {
        light: {
          root: {
            focusBorderColor: "{primary.100}",
          },
        },
      },
    },
    menubar: {
      colorScheme: {
        light: {
          root: {
            background: "#ffffff",
          },
        },
      },
    },
    splitter: {
      colorScheme: {
        light: {
          root: {
            background: "#f3f3f3;",
          },
        },
      },
    },
  },
});
